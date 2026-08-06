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

import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.ui.SideBar;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginComponent;
import com.owlplug.plugin.model.PluginDirectory;
import com.owlplug.plugin.model.Symlink;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class NodeInfoController extends BaseController {

  @Autowired
  private PluginInfoController pluginInfoController;
  @Autowired
  private DirectoryInfoController directoryInfoController;
  @Autowired
  private SymlinkInfoController symlinkInfoController;
  @Autowired
  private ComponentInfoController componentInfoController;

  @FXML
  private HBox nodeInfoContainer;
  @FXML
  private AnchorPane nodeInfoContent;
  @FXML
  private Button closeButton;
  @FXML
  private Node pluginInfoView;
  @FXML
  private Node directoryInfoView;
  @FXML
  private Node symlinkInfoView;
  @FXML
  private Node componentInfoView;

  private SideBar sidebar;

  /**
   * FXML initialize.
   */
  public void initialize() {

    nodeInfoContainer.getChildren().remove(nodeInfoContent);
    sidebar = new SideBar(500, nodeInfoContent);
    nodeInfoContainer.getChildren().add(sidebar);

    closeButton.setOnAction(e -> sidebar.collapse());
    closeButton.visibleProperty().bind(pluginInfoView.visibleProperty()
        .or(componentInfoView.visibleProperty()));

    pluginInfoView.setVisible(false);
    directoryInfoView.setVisible(false);
    symlinkInfoView.setVisible(false);
    componentInfoView.setVisible(false);
  }

  public void show() {
    if (sidebar.isCollapsed()) {
      sidebar.expand();
    }
  }

  public void hide() {
    sidebar.collapse();
  }

  public void toggleVisibility() {
    sidebar.toggle();
  }

  public void setNode(Object node) {

    switch (node) {
      case Plugin plugin -> {
        pluginInfoController.pluginProperty().set(plugin);
        pluginInfoView.setVisible(true);
        directoryInfoView.setVisible(false);
        symlinkInfoView.setVisible(false);
        componentInfoView.setVisible(false);
      }
      case PluginDirectory pluginDirectory -> {
        directoryInfoController.pluginDirectoryProperty().set(pluginDirectory);
        directoryInfoView.setVisible(true);
        pluginInfoView.setVisible(false);
        symlinkInfoView.setVisible(false);
        componentInfoView.setVisible(false);
      }
      case Symlink symlink -> {
        symlinkInfoController.symlinkProperty().set(symlink);
        symlinkInfoView.setVisible(true);
        pluginInfoView.setVisible(false);
        directoryInfoView.setVisible(false);
        componentInfoView.setVisible(false);
      }
      case PluginComponent pluginComponent -> {
        componentInfoController.componentProperty().set(pluginComponent);
        componentInfoView.setVisible(true);
        pluginInfoView.setVisible(false);
        symlinkInfoView.setVisible(false);
        directoryInfoView.setVisible(false);
      }
      case null, default -> {
        // do nothing otherwise
        // Previously, all view where hidden, but it created
        // rendering bugs when file stat PieChart is hidden while animation is running.
        // Some text nodes remains displayed and cannot be cleared.
      }
    }
  }
}
