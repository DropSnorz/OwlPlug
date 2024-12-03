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

import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.controllers.dialogs.ExportDialogController;
import com.owlplug.core.controllers.dialogs.NewLinkController;
import com.owlplug.core.dao.PluginDAO;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.services.PluginService;
import com.owlplug.core.ui.FilterableTreeItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import jfxtras.styles.jmetro.JMetroStyleClass;

@Controller
public class PluginsController extends BaseController {

  @Autowired
  private PluginService pluginService;
  @Autowired
  private PluginDAO pluginDAO;
  @Autowired
  private NodeInfoController nodeInfoController;
  @Autowired
  private NewLinkController newLinkController;
  @Autowired
  private ExportDialogController exportDialogController;
  @Autowired
  protected CoreTaskFactory taskFactory;
  @Autowired
  protected PluginTreeViewController treeViewController;

  @FXML
  private Button syncButton;
  @FXML
  private Button exportButton;
  @FXML
  private TabPane pluginTreeViewTabPane;
  @FXML
  private TextField searchTextField;
  @FXML
  private Button newLinkButton;
  @FXML
  private VBox pluginInfoPane;
  @FXML
  private VBox pluginsContainer;
  @FXML
  private FilteredTableView<Plugin> pluginTableView;

  /**
   * FXML initialize method.
   */
  @FXML
  public void initialize() {

    newLinkButton.setOnAction(e -> {
      newLinkController.show();
    });

    pluginsContainer.getChildren().add(treeViewController.getTreeViewNode());

    // Dispatches treeView selection event to the nodeInfoController
    treeViewController.getTreeViewNode().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        TreeItem<Object> selectedItem = newValue;
        nodeInfoController.setNode(selectedItem.getValue());
      }
    });

    treeViewController.searchProperty().bind(searchTextField.textProperty());

    pluginTreeViewTabPane.getStyleClass().add(JMetroStyleClass.UNDERLINE_TAB_PANE);

    // Handles tabPane selection event and toggles displayed treeView
    pluginTreeViewTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
      if (newTab.getId().equals("treeTabAll")) {
        treeViewController.setDisplay(PluginTreeViewController.Display.FlatTree);
        // TODO setVisible / managed in subcontroller
        treeViewController.getTreeViewNode().setVisible(true);
        treeViewController.getTreeViewNode().setManaged(true);
        pluginInfoPane.setManaged(true);
        pluginInfoPane.setVisible(true);
        pluginTableView.setManaged(false);
        pluginTableView.setVisible(false);
      } else if (newTab.getId().equals("treeTabDirectories")) {
        treeViewController.setDisplay(PluginTreeViewController.Display.DirectoryTree);
        // TODO setVisible / managed in subcontroller
        treeViewController.getTreeViewNode().setManaged(true);
        treeViewController.getTreeViewNode().setVisible(true);
        pluginInfoPane.setManaged(true);
        pluginInfoPane.setVisible(true);
        pluginTableView.setManaged(false);
        pluginTableView.setVisible(false);
      } else {
        treeViewController.getTreeViewNode().setManaged(false);
        treeViewController.getTreeViewNode().setVisible(false);
        pluginInfoPane.setManaged(false);
        pluginInfoPane.setVisible(false);
        pluginTableView.setManaged(true);
        pluginTableView.setVisible(true);
      }
    });

    syncButton.setOnAction(e -> {
      this.getAnalyticsService().pageView("/app/core/action/syncPlugins");
      pluginService.syncPlugins();
    });

    taskFactory.addSyncPluginsListener(() -> clearAndFillPluginTree());

    exportButton.setOnAction(e -> {
      exportDialogController.show();
    });

    this.clearAndFillPluginTree();

  }

  public void clearAndFillPluginTree() {
    Iterable<Plugin> plugins = pluginDAO.findAll();
    treeViewController.clearAndFillPluginTree(plugins);
  }

  public void selectPluginInTreeById(long id) {
    treeViewController.selectPluginInTreeById(id);
  }
  
  public void refreshPluginTree() {
    treeViewController.refresh();
  }

}
