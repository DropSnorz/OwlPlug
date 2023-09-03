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
 
package com.owlplug.core.controllers;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.dao.FileStatDAO;
import com.owlplug.core.model.FileStat;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginDirectory;
import com.owlplug.core.tasks.DirectoryRemoveTask;
import com.owlplug.core.ui.DoughnutChart;
import com.owlplug.core.ui.PluginListCellFactory;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.core.utils.PlatformUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DirectoryInfoController extends BaseController {

  @Autowired
  private CoreTaskFactory taskFactory;
  @Autowired
  private FileStatDAO fileStatDAO;

  @FXML
  private Label directoryPathLabel;
  @FXML
  private Label directoryMetricsLabel;
  @FXML
  private ListView<Plugin> pluginDirectoryListView;
  @FXML
  private Button openDirectoryButton;
  @FXML
  private Button deleteDirectoryButton;
  @FXML
  private VBox pieChartContainer;

  private PieChart pieChart;

  private PluginDirectory pluginDirectory;

  /**
   * FXML Initialize.
   */
  public void initialize() {

    openDirectoryButton.setOnAction(e -> {
      PlatformUtils.openDirectoryExplorer(pluginDirectory.getPath());
    });

    pluginDirectoryListView.setCellFactory(new PluginListCellFactory(this.getApplicationDefaults()));

    deleteDirectoryButton.setOnAction(e -> {
      JFXDialog dialog = this.getDialogManager().newDialog();
      JFXDialogLayout layout = new JFXDialogLayout();

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
              String label = ellipsisString(d.getName(), 15, 3)
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

  }

  public void setPluginDirectory(PluginDirectory pluginDirectory) {
    this.pluginDirectory = pluginDirectory;
    directoryPathLabel.setText(pluginDirectory.getPath());
    pluginDirectoryListView.getItems().setAll(pluginDirectory.getPluginList());


    String path = pluginDirectory.getPath();
    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }

    List<String> directoryMetrics = new ArrayList<>();
    Optional<FileStat> directoryStat = fileStatDAO.findByPath(path);
    directoryStat.ifPresent(fileStat -> directoryMetrics.add(
            FileUtils.humanReadableByteCount(fileStat.getLength(), true)));
    directoryMetrics.add(pluginDirectory.getPluginList().size() + " plugin(s)");

    List<FileStat> fileStats = fileStatDAO.findByParentPathOrderByLengthDesc(path);
    if (fileStats.size() > 0) {
      directoryMetrics.add(fileStats.size() + " file(s)");
    }

    pieChart.setData(createStatChartBuckets(fileStats));
    pieChart.layout();

    directoryMetricsLabel.setText(String.join(" | ", directoryMetrics));
    
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

  private String ellipsisString(String input, int maxLength, int clearEndLength) {
    if (input == null || input.length() <= maxLength
        || clearEndLength >= maxLength) {
      return input;
    } else {
      String truncatedString = input.substring(0, maxLength - clearEndLength);
      return truncatedString + "..." + input.substring(input.length() - clearEndLength);
    }
  }

}
