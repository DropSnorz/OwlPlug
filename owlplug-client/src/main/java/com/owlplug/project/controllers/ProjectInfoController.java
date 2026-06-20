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
import com.owlplug.plugin.ui.PluginFormatBadgeView;
import com.owlplug.core.controllers.MainController;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.core.utils.TimeUtils;
import com.owlplug.plugin.controllers.PluginsController;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.project.model.DawPlugin;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.model.LookupResult;
import java.io.File;
import javafx.beans.property.ObjectProperty;
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
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectInfoController extends BaseController {

  @Autowired
  private PluginsController pluginsController;
  @Autowired
  @Lazy
  private MainController mainController;

  @FXML
  private VBox projectInfoPane;
  @FXML
  private Pane projectScreenshotPane;
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

  private final ObjectProperty<DawProject> projectProperty = new SimpleObjectProperty<>();


  @FXML
  public void initialize() {

    projectScreenshotPane.setEffect(new ColorAdjust(0, 0, -0.8, 0));
    projectProperty.addListener(e -> refresh());
    openDirectoryButton.setOnAction(e -> {
      File projectFile = new File(projectPathLabel.getText());
      PlatformUtils.openFromDesktop(projectFile.getParentFile());
    });

    projectOpenButton.setOnAction(e -> {
      DawProject project = projectProperty.get();
      if (project != null) {
        PlatformUtils.openFromDesktop(project.getPath());
        // Disable to prevent opening the project several times.
        projectOpenButton.setDisable(true);
      }
    });

    // Set invisible by default if no project is selected.
    projectInfoPane.setVisible(false);
    pluginTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

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

    pluginTableStatusColumn.setCellFactory(e -> new TableCell<>() {
      @Override
      public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (item == null || empty) {
          setGraphic(null);
        } else {
          String iconLiteral;
          String iconStyleClass;
          String labelStyleClass;
          if (item.equals(LookupResult.FOUND.getValue())) {
            iconLiteral = "mdi2c-check";
            iconStyleClass = "status-icon-found";
            labelStyleClass = "label-success";
          } else if (item.equals(LookupResult.MISSING.getValue())) {
            iconLiteral = "mdi2a-alert-circle-outline";
            iconStyleClass = "status-icon-missing";
            labelStyleClass = "label-danger";
          } else {
            iconLiteral = "mdi2h-help-circle-outline";
            iconStyleClass = "status-icon-unknown";
            labelStyleClass = "label-disabled";
          }
          FontIcon icon = new FontIcon(iconLiteral);
          icon.getStyleClass().add(iconStyleClass);
          Label label = new Label(item);
          label.getStyleClass().add(labelStyleClass);
          HBox badge = new HBox(5, icon, label);
          badge.getStyleClass().add("lookup-status-badge");
          setGraphic(badge);
        }
      }
    });

    pluginTableLinkColumn.setCellValueFactory(cellData -> {
      if (cellData.getValue().getLookup() != null) {
        return new SimpleObjectProperty<>(cellData.getValue().getLookup().getPlugin());
      }
      return null;
    });

    pluginTableLinkColumn.setCellFactory(e -> new TableCell<>() {
      @Override
      public void updateItem(Plugin item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
          setGraphic(null);
        } else {
          Hyperlink link = new Hyperlink();
          link.setGraphic(new FontIcon("mdi2p-power-plug-outline"));
          link.setOnAction(ev -> {
            pluginsController.selectPluginById(item.getId());
            mainController.navigateToMainTab(MainController.PLUGINS_TAB_INDEX);
          });
          setGraphic(link);
        }
      }
    });

    pluginTableFormatColumn.setCellFactory(e -> new TableCell<>() {
      @Override
      public void updateItem(PluginFormat item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
          setGraphic(null);
        } else {
          setText(null);
          setGraphic(new HBox(new PluginFormatBadgeView(
              item, getApplicationDefaults(), PluginFormatBadgeView.DisplayMode.DEFAULT)));
        }
      }
    });

  }

  public void refresh() {
    DawProject project = projectProperty.get();
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

    BackgroundImage bgImg = new BackgroundImage(this.getApplicationDefaults().getDawApplicationImage(project.getApplication()),
        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
        new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
    projectScreenshotPane.setBackground(new Background(bgImg));

  }

  public ObjectProperty<DawProject> projectProperty() {
    return projectProperty;
  }

}
