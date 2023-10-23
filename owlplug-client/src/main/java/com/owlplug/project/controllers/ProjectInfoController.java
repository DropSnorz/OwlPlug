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
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.project.model.Project;
import com.owlplug.project.model.ProjectPlugin;
import java.io.File;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectInfoController extends BaseController {

  @FXML
  private VBox projectInfoPane;
  @FXML
  private Label projectNameLabel;
  @FXML
  private Label projectAppLabel;
  @FXML
  private Label appFullNameLabel;
  @FXML
  private Label projectPathLabel;
  @FXML
  private Button openDirectoryButton;
  @FXML
  private TableView<ProjectPlugin> pluginTable;
  @FXML
  private TableColumn<ProjectPlugin, PluginFormat> pluginTableFormatColumn;
  @FXML
  private TableColumn<ProjectPlugin, String> pluginTableNameColumn;
  @FXML
  private TableColumn<ProjectPlugin, String> pluginTableStatusColumn;


  @FXML
  public void initialize() {
    openDirectoryButton.setOnAction(e -> {
      File projectFile = new File(projectPathLabel.getText());
      PlatformUtils.openDirectoryExplorer(projectFile.getParentFile());
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
    pluginTableStatusColumn.setCellFactory(e -> new TableCell<ProjectPlugin, String>() {
      @Override
      public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
          this.getStyleClass().remove("cell-undefined-link");
        } else {
          setText(item);
          this.getStyleClass().add("cell-undefined-link");
        }
      }
    });

    pluginTableFormatColumn.setCellFactory(e -> new TableCell<ProjectPlugin, PluginFormat>() {
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

  public void setProject(Project project) {
    projectInfoPane.setVisible(true);
    projectNameLabel.setText(project.getName());
    projectAppLabel.setText(project.getApplication().getName());
    appFullNameLabel.setText(project.getAppFullName());
    projectPathLabel.setText(project.getPath());

    pluginTable.setItems(FXCollections.observableList(project.getPlugins().stream().toList()));

  }

}
