/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.controllers.dialogs.DialogController;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginDirectory;
import com.owlplug.core.tasks.DirectoryRemoveTask;
import com.owlplug.core.ui.PluginListCellFactory;
import com.owlplug.core.utils.PlatformUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DirectoryInfoController {

  @Autowired
  private ApplicationDefaults applicationDefaults;
  @Autowired
  private DialogController dialogController;
  @Autowired
  private CoreTaskFactory taskFactory;

  @FXML
  private Label directoryPathLabel;
  @FXML
  private JFXListView<Plugin> pluginDirectoryListView;
  @FXML
  private JFXButton openDirectoryButton;
  @FXML
  private JFXButton deleteDirectoryButton;

  private PluginDirectory pluginDirectory;

  /**
   * FXML Initialize.
   */
  public void initialize() {

    openDirectoryButton.setGraphic(new ImageView(applicationDefaults.directoryImage));
    openDirectoryButton.setOnAction(e -> {
      PlatformUtils.openDirectoryExplorer(pluginDirectory.getPath());
    });

    pluginDirectoryListView.setCellFactory(new PluginListCellFactory(applicationDefaults));

    deleteDirectoryButton.setOnAction(e -> {
      JFXDialog dialog = dialogController.newDialog();
      JFXDialogLayout layout = new JFXDialogLayout();

      layout.setHeading(new Label("Remove directory"));
      layout.setBody(new Label("Do you really want to remove " + pluginDirectory.getName()
          + " and all of its content ? This will permanently delete the file from your hard drive."));

      JFXButton cancelButton = new JFXButton("Cancel");

      cancelButton.setOnAction(cancelEvent -> {
        dialog.close();
      });

      JFXButton removeButton = new JFXButton("Remove");
      removeButton.setOnAction(removeEvent -> {
        dialog.close();
        taskFactory.create(new DirectoryRemoveTask(pluginDirectory))
            .setOnSucceeded(x -> taskFactory.createPluginSyncTask(pluginDirectory.getPath()).schedule())
          .schedule();
      });
      removeButton.getStyleClass().add("button-danger");

      layout.setActions(removeButton, cancelButton);
      dialog.setContent(layout);
      dialog.show();
    });
  }

  public void setPluginDirectory(PluginDirectory pluginDirectory) {
    this.pluginDirectory = pluginDirectory;
    directoryPathLabel.setText(pluginDirectory.getPath());
    pluginDirectoryListView.getItems().setAll(pluginDirectory.getPluginList());
  }

}
