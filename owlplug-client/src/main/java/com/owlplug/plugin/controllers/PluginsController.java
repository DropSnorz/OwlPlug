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

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.plugin.components.PluginTaskFactory;
import com.owlplug.plugin.controllers.dialogs.ExportDialogController;
import com.owlplug.plugin.controllers.dialogs.NewLinkController;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.repositories.PluginRepository;
import com.owlplug.plugin.services.PluginService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PluginsController extends BaseController {

  @Autowired
  private PluginService pluginService;
  @Autowired
  private PluginRepository pluginRepository;
  @Autowired
  private NodeInfoController nodeInfoController;
  @Autowired
  private NewLinkController newLinkController;
  @Autowired
  private ExportDialogController exportDialogController;
  @Autowired
  protected PluginTaskFactory taskFactory;
  @Autowired
  protected PluginTreeViewController treeViewController;
  @Autowired
  protected PluginTableController tableController;

  @FXML
  private Button syncButton;
  @FXML
  private Button exportButton;
  @FXML
  private TabPane displaySwitchTabPane;
  @FXML
  private Tab displayListTab;
  @FXML
  private Tab displayDirectoriesTab;
  @FXML
  private Tab displayTableTab;

  @FXML
  private TextField searchTextField;
  @FXML
  private Button newLinkButton;
  @FXML
  private VBox pluginInfoPane;
  @FXML
  private VBox pluginsContainer;

  /**
   * FXML initialize method.
   */
  @FXML
  public void initialize() {

    newLinkButton.setOnAction(e -> {
      newLinkController.show();
    });

    // Add Plugin Table and TreeView to the scene graph
    pluginsContainer.getChildren().add(treeViewController.getTreeView());
    pluginsContainer.getChildren().add(tableController.getTableView());

    /* ===================
     * Plugins TreeView properties init and bindings
     * ===================
     */

    // Dispatches treeView selection event to the nodeInfoController
    treeViewController.getTreeView().getSelectionModel()
        .selectedItemProperty().addListener((observable, oldValue, newValue) -> {
          if (newValue != null) {
            nodeInfoController.setNode(newValue.getValue());
            setInfoPaneDisplay(true);
          }
        });
    treeViewController.getTreeView().setOnMouseClicked(mouseEvent -> {
      if (mouseEvent.getClickCount() == 2) {
        toggleInfoPaneDisplay();
      }
    });
    treeViewController.searchProperty().bind(searchTextField.textProperty());

    /* ===================
     * Plugins Table properties init and bindings
     * ===================
     */

    tableController.getTableView().setOnMouseClicked(mouseEvent -> {
      if (mouseEvent.getClickCount() == 2) {
        toggleInfoPaneDisplay();
      }
    });
    tableController.getTableView().getSelectionModel()
        .selectedItemProperty().addListener((observable, oldValue, newValue) -> {
          if (newValue != null) {
            nodeInfoController.setNode(newValue);
          }
        });

    tableController.searchProperty().bind(searchTextField.textProperty());


    /* ===================
     * Controller and node graph initialization
     * ===================
     */

    displaySwitchTabPane.getStyleClass().add(JMetroStyleClass.UNDERLINE_TAB_PANE);

    // Set default display (flat plugin tree)
    treeViewController.setDisplayMode(PluginTreeViewController.Display.FlatTree);
    treeViewController.getTreeView().setVisible(true);
    treeViewController.getTreeView().setManaged(true);
    tableController.getTableView().setManaged(false);
    tableController.getTableView().setVisible(false);
    setInfoPaneDisplay(true);

    // Handles tabPane selection event and toggles displayed treeView
    displaySwitchTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
      if (newTab.equals(displayListTab)) {
        treeViewController.setDisplayMode(PluginTreeViewController.Display.FlatTree);
        treeViewController.setNodeManaged(true);
        tableController.setNodeManaged(false);
        setInfoPaneDisplay(true);
        this.getPreferences().put(ApplicationDefaults.PLUGIN_PREFERRED_DISPLAY_KEY, "LIST");
      } else if (newTab.equals(displayDirectoriesTab)) {
        treeViewController.setDisplayMode(PluginTreeViewController.Display.DirectoryTree);
        treeViewController.setNodeManaged(true);
        tableController.setNodeManaged(false);
        setInfoPaneDisplay(true);
        this.getPreferences().put(ApplicationDefaults.PLUGIN_PREFERRED_DISPLAY_KEY, "DIRECTORIES");
      } else {
        treeViewController.setNodeManaged(false);
        tableController.setNodeManaged(true);
        setInfoPaneDisplay(false);
        this.getPreferences().put(ApplicationDefaults.PLUGIN_PREFERRED_DISPLAY_KEY, "TABLE");
      }
    });

    if (this.getPreferences().get(ApplicationDefaults.PLUGIN_PREFERRED_DISPLAY_KEY, "").equals("TABLE")) {
      displaySwitchTabPane.getSelectionModel().select(displayTableTab);
    } else if (this.getPreferences().get(ApplicationDefaults.PLUGIN_PREFERRED_DISPLAY_KEY, "").equals("DIRECTORIES")) {
      displaySwitchTabPane.getSelectionModel().select(displayDirectoriesTab);
    } else {
      displaySwitchTabPane.getSelectionModel().select(displayListTab);
    }

    syncButton.setOnAction(e -> {
      this.getTelemetryService().event("/Plugins/Scan");
      pluginService.syncPlugins(() -> {
          syncButton.setDisable(false);
          exportButton.setDisable(false);
      });
      syncButton.setDisable(true);
      exportButton.setDisable(true);
    });

    taskFactory.addSyncPluginsListener(this::displayPlugins);

    exportButton.setOnAction(e -> {
      this.getTelemetryService().event("/Plugins/Export");
      exportDialogController.show();
    });

    this.displayPlugins();

  }

  public void displayPlugins() {
    Iterable<Plugin> plugins = pluginRepository.findAll();
    treeViewController.setPlugins(plugins);
    tableController.setPlugins(plugins);
  }

  public void selectPluginById(long id) {
    if (displaySwitchTabPane.getSelectionModel().getSelectedItem().equals(displayTableTab)) {
      tableController.selectPluginById(id);
    } else {
      treeViewController.selectPluginById(id);
    }
  }
  
  public void refresh() {
    treeViewController.refresh();
    tableController.refresh();
  }

  public void setInfoPaneDisplay(boolean display) {
    pluginInfoPane.setManaged(display);
    pluginInfoPane.setVisible(display);
  }

  public void toggleInfoPaneDisplay() {
    pluginInfoPane.setManaged(!pluginInfoPane.isManaged());
    pluginInfoPane.setVisible(!pluginInfoPane.isVisible());
  }

}
