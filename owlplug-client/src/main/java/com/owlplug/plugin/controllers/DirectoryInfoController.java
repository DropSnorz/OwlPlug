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

import com.owlplug.controls.Dialog;
import com.owlplug.controls.DialogLayout;
import com.owlplug.controls.DoughnutChart;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.core.utils.StringUtils;
import com.owlplug.plugin.components.PluginTaskFactory;
import com.owlplug.plugin.model.FileStat;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginDirectory;
import com.owlplug.plugin.repositories.FileStatRepository;
import com.owlplug.plugin.tasks.DirectoryRemoveTask;
import com.owlplug.plugin.ui.PluginListCellFactory;
import java.io.File;
import java.util.List;
import java.util.Optional;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DirectoryInfoController extends BaseController {

  @Autowired
  private PluginTaskFactory taskFactory;
  @Autowired
  private FileStatRepository fileStatRepository;

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

  private PluginDirectory pluginDirectory;

  /**
   * FXML Initialize.
   */
  public void initialize() {

    openDirectoryButton.setOnAction(e -> {
      PlatformUtils.openFromDesktop(pluginDirectory.getPath());
    });

    pluginDirectoryListView.setCellFactory(new PluginListCellFactory(this.getApplicationDefaults()));

    deleteDirectoryButton.setOnAction(e -> {
      Dialog dialog = this.getDialogManager().newDialog();
      DialogLayout layout = new DialogLayout();

      layout.setHeading(new Label("Remove directory"));
      layout.setBody(new Label("Do you really want to remove " + pluginDirectory.getName()
          + " and all of its content ? This will permanently delete the file from your hard drive."));

      Button cancelButton = new Button("Cancel");

      cancelButton.setOnAction(cancelEvent -> {
        dialog.close();
      });

      Button removeButton = new Button("Remove");
      removeButton.setOnAction(removeEvent -> {
        dialog.close();
        taskFactory.create(new DirectoryRemoveTask(pluginDirectory))
            .setOnSucceeded(x -> taskFactory.createPluginSyncTask(pluginDirectory.getPath()).schedule())
            .schedule();
      });
      removeButton.getStyleClass().add("button-danger");

      layout.setActions(removeButton, cancelButton);
      dialog.setContent(layout);
      dialog.show();
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

    fileSizeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                    FileUtils.humanReadableByteCount(
                            cellData.getValue().getLength(), true)));

  }

  public void setPluginDirectory(PluginDirectory pluginDirectory) {
    this.pluginDirectory = pluginDirectory;
    directoryPathTextField.setText(pluginDirectory.getPath());
    directoryNameLabel.setText(pluginDirectory.getName());
    pluginDirectoryListView.getItems().setAll(pluginDirectory.getPluginList());
    directoryMetricsTab.setText("0 KB");

    File file = new File(pluginDirectory.getPath());
    deleteDirectoryButton.setDisable(!file.canWrite());

    String path = pluginDirectory.getPath();
    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }

    Optional<FileStat> directoryStat = fileStatRepository.findByPath(path);
    directoryStat.ifPresent(fileStat -> directoryMetricsTab.setText(
            FileUtils.humanReadableByteCount(fileStat.getLength(), true)));

    directoryPluginsTab.setText("Plugins (" + pluginDirectory.getPluginList().size() + ")");

    List<FileStat> fileStats = fileStatRepository.findByParentPathOrderByLengthDesc(path);
    directoryFilesTab.setText("Files (" + fileStats.size() + ")");

    ObservableList<FileStat> obsStats = FXCollections.observableArrayList();
    obsStats.addAll(fileStats);
    directoryFilesTableView.setItems(obsStats);

    pieChart.setData(createStatChartBuckets(fileStats));
    pieChart.layout();

  }

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


}
