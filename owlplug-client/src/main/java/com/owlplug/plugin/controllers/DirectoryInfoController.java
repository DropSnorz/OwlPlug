/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.plugin.controllers;

import com.owlplug.controls.DoughnutChart;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.utils.Async;
import com.owlplug.core.utils.FX;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.core.utils.StringUtils;
import com.owlplug.plugin.controllers.dialogs.RemoveDirectoryDialogController;
import com.owlplug.plugin.model.FileStat;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginDirectory;
import com.owlplug.plugin.repositories.FileStatRepository;
import com.owlplug.plugin.ui.PluginListCellFactory;
import java.io.File;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DirectoryInfoController extends BaseController {

  @Autowired
  private RemoveDirectoryDialogController removeDirectoryDialogController;
  @Autowired
  private FileStatRepository fileStatRepository;

  // One sequence per refresh slot: guarantees that only the result of the
  // latest refresh() call is applied to the UI, even if an older DB query
  // resolves later.
  private final Async.Sequence refreshSequence = new Async.Sequence();

  @FXML
  private Label directoryNameLabel;
  @FXML
  private TextField directoryPathTextField;
  @FXML
  private ListView<Plugin> pluginDirectoryListView;
  @FXML
  private Button openDirectoryButton;
  @FXML
  private Button deleteDirectoryButton;
  @FXML
  private VBox pieChartContainer;
  @FXML
  private Tab directoryMetricsTab;
  @FXML
  private Tab directoryPluginsTab;
  @FXML
  private Tab directoryFilesTab;
  @FXML
  private TableView<FileStat> directoryFilesTableView;
  @FXML
  private TableColumn<FileStat, String> fileNameColumn;
  @FXML
  private TableColumn<FileStat, String> fileSizeColumn;
  private PieChart pieChart;


  private final ObjectProperty<PluginDirectory> pluginDirectoryProperty = new SimpleObjectProperty<>();

  /**
   * FXML Initialize.
   */
  public void initialize() {

    pluginDirectoryProperty.addListener(e -> refresh());

    openDirectoryButton.setOnAction(e -> {
      PlatformUtils.openFromDesktop(pluginDirectoryProperty.get().getPath());
    });

    pluginDirectoryListView.setCellFactory(new PluginListCellFactory(this.getApplicationDefaults()));

    deleteDirectoryButton.setOnAction(e -> {
      removeDirectoryDialogController.setDirectory(pluginDirectoryProperty.get());
      removeDirectoryDialogController.show();
    });

    pieChart = new DoughnutChart() {
      @Override
      protected void layoutChartChildren(double top, double left, double contentWidth, double contentHeight) {
        if (getLabelsVisible()) {
          getData().forEach(d -> {
            Optional<Node> opTextNode = this.lookupAll(".chart-pie-label").stream().filter(
                n -> n instanceof Text && ((Text) n).getText().equals(d.getName())).findAny();
            if (opTextNode.isPresent()) {
              String label = StringUtils.ellipsis(d.getName(), 15, 3)
                                 + " - " + FileUtils.humanReadableByteCount((long) d.getPieValue(), true);
              ((Text) opTextNode.get()).setText(label);
            }
          });
        }
        super.layoutChartChildren(top, left, contentWidth, contentHeight);
      }
    };

    pieChartContainer.getChildren().add(pieChart);
    VBox.setVgrow(pieChart, Priority.ALWAYS);

    fileNameColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getName()));
    fileNameColumn.setCellFactory(e -> new TableCell<>() {
      @Override
      public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
          setGraphic(null);
        } else {
          setText(item);
          boolean isDirectory = new File(getTableRow().getItem().getPath()).isDirectory();
          setGraphic(new FontIcon(isDirectory ? "mdi2f-folder" : "mdi2f-file-outline"));
        }
      }
    });

    fileSizeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                    FileUtils.humanReadableByteCount(
                            cellData.getValue().getLength(), true)));

  }

  /**
   * Refresh directory info.
   */
  public void refresh() {
    PluginDirectory pluginDirectory = pluginDirectoryProperty.get();

    directoryPathTextField.setText(pluginDirectory.getPath());
    directoryNameLabel.setText(pluginDirectory.getName());
    pluginDirectoryListView.getItems().setAll(pluginDirectory.getPluginList());
    directoryPluginsTab.setText("Plugins (" + pluginDirectory.getPluginList().size() + ")");
    directoryMetricsTab.setText("0 KB");
    deleteDirectoryButton.setDisable(!new File(pluginDirectory.getPath()).canWrite());

    String path = pluginDirectory.getPath();
    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
    final String resolvedPath = path;

    refreshSequence.supply(() -> new FileStatResults(
        fileStatRepository.findByPath(resolvedPath),
        fileStatRepository.findByParentPathOrderByLengthDesc(resolvedPath)
    )).thenAccept(results -> FX.run(() -> {
      results.directoryStat().ifPresent(fileStat ->
          directoryMetricsTab.setText(FileUtils.humanReadableByteCount(fileStat.getLength(), true)));
      directoryFilesTab.setText("Files (" + results.fileStats().size() + ")");
      directoryFilesTableView.setItems(FXCollections.observableArrayList(results.fileStats()));
      pieChart.setData(createStatChartBuckets(results.fileStats()));

    }));
  }

  private record FileStatResults(Optional<FileStat> directoryStat, List<FileStat> fileStats) {}

  private ObservableList<PieChart.Data> createStatChartBuckets(List<FileStat> fileStats) {
    ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
    int i = 0;
    int maxBucket = 7;
    while (i < fileStats.size() && i < maxBucket) {
      chartData.add(new PieChart.Data(fileStats.get(i).getName(), fileStats.get(i).getLength()));
      i = i + 1;
    }

    if (i < fileStats.size()) {
      long groupLength = 0;
      while (i < fileStats.size()) {
        groupLength += fileStats.get(i).getLength();
        i = i + 1;
      }
      chartData.add(new PieChart.Data("Others", groupLength));
    }

    return chartData;
  }

  public ObjectProperty<PluginDirectory> pluginDirectoryProperty() {
    return pluginDirectoryProperty;
  }


}
