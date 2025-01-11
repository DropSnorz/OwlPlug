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

package com.owlplug.project.controllers;

import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.controllers.MainController;
import com.owlplug.core.controllers.PluginsController;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.core.utils.TimeUtils;
import com.owlplug.project.model.LookupResult;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.model.DawPlugin;
import java.io.File;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectInfoController extends BaseController {

  @Autowired
  private PluginsController pluginsController;
  @Autowired
  private MainController mainController;

  @FXML
  private VBox projectInfoPane;
  @FXML
  private Label projectNameLabel;
  @FXML
  private ImageView projectAppImageView;
  @FXML
  private Label projectAppLabel;
  @FXML
  private Button projectOpenButton;
  @FXML
  private Label appFullNameLabel;
  @FXML
  private Label projectFormatVersionLabel;
  @FXML
  private Label projectCreatedLabel;
  @FXML
  private Label projectLastModifiedLabel;
  @FXML
  private Label projectPluginsFoundLabel;
  @FXML
  private Label projectPathLabel;
  @FXML
  private Button openDirectoryButton;
  @FXML
  private TableView<DawPlugin> pluginTable;
  @FXML
  private TableColumn<DawPlugin, PluginFormat> pluginTableFormatColumn;
  @FXML
  private TableColumn<DawPlugin, String> pluginTableNameColumn;
  @FXML
  private TableColumn<DawPlugin, String> pluginTableStatusColumn;
  @FXML
  private TableColumn<DawPlugin, Plugin> pluginTableLinkColumn;

  private DawProject currentProject = null;


  @FXML
  public void initialize() {
    openDirectoryButton.setOnAction(e -> {
      File projectFile = new File(projectPathLabel.getText());
      PlatformUtils.openFromDesktop(projectFile.getParentFile());
    });

    projectOpenButton.setOnAction(e -> {
      if (currentProject != null) {
        PlatformUtils.openFromDesktop(currentProject.getPath());
        // Disable to prevent opening the project several times.
        projectOpenButton.setDisable(true);
      }
    });

    // Set invisible by default if no project is selected.
    projectInfoPane.setVisible(false);

    pluginTableNameColumn.setCellValueFactory(cellData -> {
      return new SimpleStringProperty(cellData.getValue().getName());
    });
    pluginTableStatusColumn.setCellValueFactory(cellData -> {
      if (cellData.getValue().getLookup() != null
              && cellData.getValue().getLookup().getResult() != null) {
        return new SimpleStringProperty(cellData.getValue().getLookup().getResult().getValue());
      }
      return new SimpleStringProperty("Unknown");
    });
    pluginTableFormatColumn.setCellValueFactory(cellData -> {
      return new SimpleObjectProperty<>(cellData.getValue().getFormat());
    });

    pluginTableStatusColumn.setCellFactory(e -> new TableCell<DawPlugin, String>() {
      @Override
      public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        this.getStyleClass().remove("cell-unknown-link");
        this.getStyleClass().remove("cell-missing-link");
        this.getStyleClass().remove("cell-found-link");
        if (item == null || empty) {
          setText(null);
        } else {
          setText(item);
          if (item.equals(LookupResult.MISSING.getValue())) {
            this.getStyleClass().add("cell-missing-link");
          } else if (item.equals(LookupResult.FOUND.getValue())) {
            this.getStyleClass().add("cell-found-link");
          } else {
            this.getStyleClass().add("cell-unknown-link");
          }
        }
      }
    });

    pluginTableLinkColumn.setCellValueFactory(cellData -> {
      if (cellData.getValue().getLookup() != null) {
        return new SimpleObjectProperty<>(cellData.getValue().getLookup().getPlugin());
      }
      return null;
    });

    pluginTableLinkColumn.setCellFactory(e -> new TableCell<DawPlugin, Plugin>() {
      @Override
      public void updateItem(Plugin item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
          setGraphic(null);
        } else {
          Hyperlink link = new Hyperlink();
          link.setGraphic(new ImageView(getApplicationDefaults().linkIconImage));
          link.setOnAction(e -> {
            pluginsController.selectPluginById(item.getId());
            mainController.selectMainTab(MainController.PLUGINS_TAB_INDEX);
          });
          setGraphic(link);
        }
      }
    });

    pluginTableFormatColumn.setCellFactory(e -> new TableCell<DawPlugin, PluginFormat>() {
      @Override
      public void updateItem(PluginFormat item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
          setGraphic(null);
        } else {
          setText(item.getText());
          setGraphic(new ImageView(getApplicationDefaults().getPluginFormatIcon(item)));
        }
      }
    });

  }

  public void setProject(DawProject project) {
    this.currentProject = project;
    projectInfoPane.setVisible(true);
    projectNameLabel.setText(project.getName());
    projectAppLabel.setText(project.getApplication().getName());
    projectAppImageView.setImage(this.getApplicationDefaults().getDAWApplicationIcon(project.getApplication()));
    projectOpenButton.setDisable(false);
    appFullNameLabel.setText(project.getAppFullName());
    projectCreatedLabel.setText(TimeUtils.getHumanReadableDurationFrom(project.getCreatedAt()));
    projectLastModifiedLabel.setText(TimeUtils.getHumanReadableDurationFrom(project.getLastModifiedAt()));
    projectPluginsFoundLabel.setText(String.valueOf(project.getPlugins().size()));
    projectFormatVersionLabel.setText("v" + project.getFormatVersion());
    projectPathLabel.setText(project.getPath());

    pluginTable.setItems(FXCollections.observableList(project.getPlugins().stream().toList()));

  }

}
