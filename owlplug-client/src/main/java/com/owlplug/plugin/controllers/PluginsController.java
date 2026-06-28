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
import com.owlplug.core.components.ApplicationDefaults.Prefs;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.utils.FX;
import com.owlplug.plugin.components.PluginFilterModel;
import com.owlplug.plugin.components.PluginTaskFactory;
import com.owlplug.plugin.controllers.dialogs.ExportDialogController;
import com.owlplug.plugin.controllers.dialogs.NewLinkController;
import com.owlplug.plugin.events.PluginRefreshEvent;
import com.owlplug.plugin.events.PluginScanCompletedEvent;
import com.owlplug.plugin.events.PluginUpdateEvent;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.repositories.PluginRepository;
import com.owlplug.plugin.services.PluginService;
import com.owlplug.core.utils.Async;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
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
  @Autowired
  protected PluginFilterController filterController;
  @Autowired
  protected PluginFilterModel filterModel;

  @FXML
  private SplitMenuButton scanMenuButton;
  @FXML
  private MenuItem scanMenuItem;
  @FXML
  private MenuItem fullScanMenuItem;
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
  private Button filterToggleButton;
  @FXML
  private VBox pluginInfoPane;
  @FXML
  private VBox pluginsContainer;
  @FXML
  private HBox filterContainer;

  /**
   * FXML initialize method.
   */
  @FXML
  public void initialize() {

    newLinkButton.setOnAction(e -> {
      newLinkController.show();
    });

    // Wire filter sidebar and predicate into sub-controllers
    filterContainer.getChildren().add(0, filterController.getView());
    tableController.bindFilterModel(filterModel);
    treeViewController.bindFilterModel(filterModel);

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
        this.getPreferences().put(Prefs.Plugins.PREFERRED_DISPLAY, "LIST");
      } else if (newTab.equals(displayDirectoriesTab)) {
        treeViewController.setDisplayMode(PluginTreeViewController.Display.DirectoryTree);
        treeViewController.setNodeManaged(true);
        tableController.setNodeManaged(false);
        setInfoPaneDisplay(true);
        this.getPreferences().put(Prefs.Plugins.PREFERRED_DISPLAY, "DIRECTORIES");
      } else {
        treeViewController.setNodeManaged(false);
        tableController.setNodeManaged(true);
        setInfoPaneDisplay(false);
        this.getPreferences().put(Prefs.Plugins.PREFERRED_DISPLAY, "TABLE");
      }
    });

    if (this.getPreferences().get(Prefs.Plugins.PREFERRED_DISPLAY, "").equals("TABLE")) {
      displaySwitchTabPane.getSelectionModel().select(displayTableTab);
    } else if (this.getPreferences().get(Prefs.Plugins.PREFERRED_DISPLAY, "").equals("DIRECTORIES")) {
      displaySwitchTabPane.getSelectionModel().select(displayDirectoriesTab);
    } else {
      displaySwitchTabPane.getSelectionModel().select(displayListTab);
    }

    scanMenuButton.setOnAction(e -> {
      this.getTelemetryService().event("/Plugins/Scan");
      pluginService.scanPlugins();
    });

    scanMenuItem.setOnAction(e -> {
      this.getTelemetryService().event("/Plugins/Scan");
      pluginService.scanPlugins();
    });

    fullScanMenuItem.setOnAction(e -> {
      this.getTelemetryService().event("/Plugins/FullScan");
      pluginService.scanPlugins(false);
    });

    exportButton.setOnAction(e -> {
      this.getTelemetryService().event("/Plugins/Export");
      exportDialogController.show();
    });

    this.displayPlugins();

  }

  public void displayPlugins() {
    Async.supply(() -> pluginRepository.findAll())
        .thenAccept(plugins -> FX.run(() -> {
          treeViewController.setPlugins(plugins);
          tableController.setPlugins(plugins);
          filterController.refresh(plugins);
        }));
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

  @FXML
  public void toggleFilter() {
    filterController.getView().toggle();
  }

  public void setSearch(String query) {
    searchTextField.setText(query);
  }

  public void setInfoPaneDisplay(boolean display) {
    pluginInfoPane.setManaged(display);
    pluginInfoPane.setVisible(display);
  }

  public void toggleInfoPaneDisplay() {
    pluginInfoPane.setManaged(!pluginInfoPane.isManaged());
    pluginInfoPane.setVisible(!pluginInfoPane.isVisible());
  }

  @EventListener
  private void handle(PluginScanCompletedEvent event) {
    FX.run(this::displayPlugins);
  }

  @EventListener
  private void handle(PluginRefreshEvent event) {
    FX.run(this::refresh);
  }

  @EventListener
  private void handle(PluginUpdateEvent event) {
    FX.run(this::displayPlugins);
  }

}
