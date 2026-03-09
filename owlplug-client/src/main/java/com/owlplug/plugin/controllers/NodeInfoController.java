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
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginComponent;
import com.owlplug.plugin.model.PluginDirectory;
import com.owlplug.plugin.model.Symlink;
import javafx.fxml.FXML;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class NodeInfoController extends BaseController {

  @FXML
  private PluginInfoController pluginInfoViewController;
  @FXML
  private DirectoryInfoController directoryInfoViewController;
  @FXML
  private SymlinkInfoController symlinkInfoViewController;
  @FXML
  private ComponentInfoController componentInfoViewController;

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
      pluginInfoViewController.setPlugin((Plugin) node);
      pluginInfoView.setVisible(true);
    }
    if (node instanceof PluginDirectory) {
      directoryInfoViewController.setPluginDirectory((PluginDirectory) node);
      directoryInfoView.setVisible(true);
    }
    if (node instanceof Symlink) {
      symlinkInfoViewController.setSymlink((Symlink) node);
      symlinkInfoView.setVisible(true);
    }
    if (node instanceof PluginComponent) {
      componentInfoViewController.setComponent((PluginComponent) node);
      componentInfoView.setVisible(true);
    }
  }

}
