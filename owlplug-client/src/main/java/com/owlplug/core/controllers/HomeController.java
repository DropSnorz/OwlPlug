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

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.controllers.dialogs.ListDirectoryDialogController;
import com.owlplug.core.utils.FX;
import com.owlplug.plugin.controllers.PluginsController;
import com.owlplug.plugin.events.PluginUpdateEvent;
import com.owlplug.plugin.model.FileStat;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.repositories.FileStatRepository;
import com.owlplug.plugin.repositories.PluginRepository;
import com.owlplug.plugin.services.PluginService;
import com.owlplug.project.model.LookupResult;
import com.owlplug.project.repositories.DawProjectRepository;
import com.owlplug.project.repositories.PluginLookupRepository;
import com.owlplug.project.services.ProjectService;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController extends BaseController {

  @Autowired
  private PluginRepository pluginRepository;
  @Autowired
  private DawProjectRepository dawProjectRepository;
  @Autowired
  private PluginLookupRepository pluginLookupRepository;
  @Autowired
  private FileStatRepository fileStatRepository;
  @Autowired
  private PluginService pluginService;
  @Autowired
  private ProjectService projectService;
  @Autowired
  @Lazy
  private MainController mainController;
  @Autowired
  @Lazy
  private PluginsController pluginsController;
  @Autowired
  private ListDirectoryDialogController listDirectoryDialogController;

  @FXML
  private Label pluginCountLabel;
  @FXML
  private Label vst2CountLabel;
  @FXML
  private Label vst3CountLabel;
  @FXML
  private Label auCountLabel;
  @FXML
  private Label lv2CountLabel;
  @FXML
  private Label projectCountLabel;
  @FXML
  private Label unresolvedPluginCountLabel;
  @FXML
  private Label disabledCountLabel;
  @FXML
  private Label pluginDirectoryCountLabel;
  @FXML
  private Label projectDirectoryCountLabel;

  @FXML
  private VBox pluginTile;
  @FXML
  private VBox projectTile;

  @FXML
  private VBox fileSizeChartContainer;
  @FXML
  private Label fileSizeEmptyLabel;

  @FXML
  private VBox setupPane;
  @FXML
  private VBox noPluginDirectorySuggestion;
  @FXML
  private VBox noPluginSuggestion;
  @FXML
  private VBox noProjectSuggestion;

  @FXML
  private TextField pluginSearchField;

  @FXML
  private Button scanPluginsButton;
  @FXML
  private Button exploreButton;
  @FXML
  private Button syncProjectsButton;
  @FXML
  private Button settingsButton;
  @FXML
  private Button setupPluginDirButton;
  @FXML
  private Button scanSuggestionButton;
  @FXML
  private Button setupProjectDirButton;

  private BarChart<Number, String> fileSizeChart;

  /**
   * FXML initialize method.
   */
  @FXML
  public void initialize() {
    pluginTile.setOnMouseClicked(e -> mainController.selectMainTab(MainController.PLUGINS_TAB_INDEX));
    projectTile.setOnMouseClicked(e -> mainController.selectMainTab(MainController.PROJECTS_TAB_INDEX));

    pluginSearchField.setOnAction(e -> {
      mainController.selectMainTab(MainController.PLUGINS_TAB_INDEX);
      pluginsController.setSearch(pluginSearchField.getText());
    });

    scanPluginsButton.setOnAction(e -> pluginService.scanPlugins());
    exploreButton.setOnAction(e -> mainController.selectMainTab(MainController.EXPLORE_TAB_INDEX));
    syncProjectsButton.setOnAction(e -> projectService.syncProjects());
    settingsButton.setOnAction(e -> mainController.selectMainTab(MainController.OPTIONS_TAB_INDEX));

    setupPluginDirButton.setOnAction(e -> mainController.selectMainTab(MainController.OPTIONS_TAB_INDEX));
    scanSuggestionButton.setOnAction(e -> pluginService.scanPlugins());
    setupProjectDirButton.setOnAction(e -> {
      listDirectoryDialogController.configure(ApplicationDefaults.PROJECT_DIRECTORY_KEY);
      listDirectoryDialogController.show();
    });

    initFileSizeChart();
    refresh();
  }

  private void initFileSizeChart() {
    NumberAxis sizeAxis = new NumberAxis();
    sizeAxis.setTickLabelsVisible(false);
    sizeAxis.setTickMarkVisible(false);
    sizeAxis.setMinorTickVisible(false);

    CategoryAxis nameAxis = new CategoryAxis();

    fileSizeChart = new BarChart<>(sizeAxis, nameAxis);
    fileSizeChart.setAnimated(true);
    fileSizeChart.setLegendVisible(false);
    fileSizeChart.setCategoryGap(12);
    fileSizeChart.setBarGap(0);
    fileSizeChart.setMaxWidth(Double.MAX_VALUE);
    fileSizeChart.setMaxHeight(Double.MAX_VALUE);
    fileSizeChart.getStyleClass().add("dashboard-bar-chart");
    VBox.setVgrow(fileSizeChart, javafx.scene.layout.Priority.ALWAYS);

    fileSizeChartContainer.getChildren().add(fileSizeChart);
  }

  /**
   * Refreshes all dashboard statistics and updates setup suggestion visibility.
   */
  public void refresh() {
    final long totalPlugins = pluginRepository.count();
    final long vst2Count = pluginRepository.countByFormat(PluginFormat.VST2);
    final long vst3Count = pluginRepository.countByFormat(PluginFormat.VST3);
    final long auCount = pluginRepository.countByFormat(PluginFormat.AU);
    final long lv2Count = pluginRepository.countByFormat(PluginFormat.LV2);
    final long projectCount = dawProjectRepository.count();
    final long unresolvedCount = pluginLookupRepository.countByResult(LookupResult.MISSING);
    final long disabledCount = pluginRepository.countByDisabledTrue();
    final long pluginDirectoryCount = pluginService.getDirectoriesExplorationSet().size();
    final long projectDirectoryCount = projectService.getProjectDirectories().size();


    pluginCountLabel.setText(String.valueOf(totalPlugins));
    vst2CountLabel.setText(String.valueOf(vst2Count));
    vst3CountLabel.setText(String.valueOf(vst3Count));
    auCountLabel.setText(String.valueOf(auCount));
    lv2CountLabel.setText(String.valueOf(lv2Count));
    projectCountLabel.setText(String.valueOf(projectCount));
    unresolvedPluginCountLabel.setText(String.valueOf(unresolvedCount));
    disabledCountLabel.setText(String.valueOf(disabledCount));
    pluginDirectoryCountLabel.setText(String.valueOf(pluginDirectoryCount));
    projectDirectoryCountLabel.setText(String.valueOf(projectDirectoryCount));

    final boolean hasPluginDirectories = !pluginService.getDirectoriesExplorationSet().isEmpty();
    final boolean hasProjectDirectories = !projectService.getProjectDirectories().isEmpty();
    final boolean hasPlugins = totalPlugins > 0;

    setNodeVisible(noPluginDirectorySuggestion, !hasPluginDirectories);
    setNodeVisible(noPluginSuggestion, hasPluginDirectories && !hasPlugins);
    setNodeVisible(noProjectSuggestion, !hasProjectDirectories);
    setNodeVisible(setupPane, !hasPluginDirectories || !hasPlugins || !hasProjectDirectories);

    refreshFileSizeChart();
  }

  private void refreshFileSizeChart() {
    final List<FileStat> topStats = fileStatRepository.findTop5ByParentIsNullOrderByLengthDesc();
    final boolean hasData = !topStats.isEmpty();

    fileSizeEmptyLabel.setVisible(!hasData);
    fileSizeEmptyLabel.setManaged(!hasData);
    fileSizeChart.setVisible(hasData);
    fileSizeChart.setManaged(hasData);

    if (!hasData) {
      return;
    }

    fileSizeChart.getData().clear();

    XYChart.Series<Number, String> series = new XYChart.Series<>();
    for (FileStat stat : topStats) {
      final double mb = stat.getLength() / (1024.0 * 1024.0);
      series.getData().add(new XYChart.Data<>(mb, stat.getName()));
    }
    fileSizeChart.getData().add(series);

    // Defer label and tooltip attachment until bars are laid out in the scene graph
    Platform.runLater(() -> series.getData().forEach(data ->
        topStats.stream()
            .filter(fs -> fs.getName().equals(data.getYValue()))
            .findFirst()
            .ifPresent(fs -> {
              final String sizeText = FileUtils.byteCountToDisplaySize(fs.getLength());
              Tooltip.install(data.getNode(), new Tooltip(data.getYValue() + " — " + sizeText));
              if (data.getNode() instanceof StackPane bar) {
                Label label = new Label(sizeText);
                label.getStyleClass().add("dashboard-bar-label");
                label.setMouseTransparent(true);
                StackPane.setAlignment(label, Pos.CENTER_RIGHT);
                StackPane.setMargin(label, new Insets(0, 6, 0, 0));
                bar.getChildren().add(label);

                // Clip the bar to exactly 20px, centered in the allocated slot
                Rectangle clip = new Rectangle();
                clip.setArcWidth(4);
                clip.setArcHeight(4);
                clip.setHeight(20);
                clip.widthProperty().bind(bar.widthProperty());
                clip.yProperty().bind(bar.heightProperty().subtract(20).divide(2));
                bar.setClip(clip);
              }
            })
    ));
  }

  private void setNodeVisible(VBox node, boolean visible) {
    node.setVisible(visible);
    node.setManaged(visible);
  }

  @EventListener
  private void handle(PluginUpdateEvent event) {
    FX.run(this::refresh);
  }

}