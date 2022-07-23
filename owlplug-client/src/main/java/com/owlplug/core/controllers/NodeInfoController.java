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

import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginComponent;
import com.owlplug.core.model.PluginDirectory;
import com.owlplug.core.model.Symlink;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
  private Node pluginInfoView;
  @FXML
  private Node directoryInfoView;
  @FXML
  private Node symlinkInfoView;
  @FXML
  private Node componentInfoView;

  /**
   * FXML initialize.
   */
  public void initialize() {

    pluginInfoView.setVisible(false);
    directoryInfoView.setVisible(false);
    symlinkInfoView.setVisible(false);
    componentInfoView.setVisible(false);
  }

  public void setNode(Object node) {

    pluginInfoView.setVisible(false);
    directoryInfoView.setVisible(false);
    symlinkInfoView.setVisible(false);
    componentInfoView.setVisible(false);

    if (node instanceof Plugin) {
      pluginInfoController.setPlugin((Plugin) node);
      pluginInfoView.setVisible(true);
    }
    if (node instanceof PluginDirectory) {
      directoryInfoController.setPluginDirectory((PluginDirectory) node);
      directoryInfoView.setVisible(true);
    }
    if (node instanceof Symlink) {
      symlinkInfoController.setSymlink((Symlink) node);
      symlinkInfoView.setVisible(true);
    }
    if (node instanceof PluginComponent) {
      componentInfoController.setComponent((PluginComponent) node);
      componentInfoView.setVisible(true);
    }
  }

}
