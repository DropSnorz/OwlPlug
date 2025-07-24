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


package com.owlplug.plugin.controllers.dialogs;

import com.owlplug.controls.DialogLayout;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.controllers.dialogs.AbstractDialogController;
import com.owlplug.plugin.controllers.PluginInfoController;
import com.owlplug.plugin.controllers.PluginsController;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.services.PluginService;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DisablePluginDialogController extends AbstractDialogController {

  @Autowired
  private PluginsController pluginsController;
  @Autowired
  private PluginService pluginService;
  @Autowired
  private PluginInfoController pluginInfoController;

  private Plugin plugin;

  public DisablePluginDialogController() {
    super(600,300);
  }

  @Override
  protected DialogLayout getLayout() {
    DialogLayout layout = new DialogLayout();
    layout.setHeading(new Label("Disable plugin " + plugin.getName()));
    VBox vbox = new VBox(10);
    Label dialogLabel = new Label(
            "Disabling a plugin will rename the plugin file by updating the extension. "
                    + "The suffix \".disabled\" will be appended to the filename causing the DAW to ignore the plugin. "
                    + "You can reactivate the plugin at any time from OwlPlug or by renaming the file manually.");
    dialogLabel.setWrapText(true);
    VBox.setVgrow(dialogLabel, Priority.ALWAYS);
    vbox.getChildren().add(dialogLabel);

    Label noteLabel = new Label("You may need admin privileges to rename plugin file.");
    noteLabel.getStyleClass().add("label-disabled");
    vbox.getChildren().add(noteLabel);

    CheckBox displayDialog = new CheckBox("Don't show me this message again");
    VBox.setMargin(displayDialog, new Insets(20,0,0,0));
    displayDialog.setSelected(!getPreferences().getBoolean(ApplicationDefaults.SHOW_DIALOG_DISABLE_PLUGIN_KEY, true));
    displayDialog.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().putBoolean(ApplicationDefaults.SHOW_DIALOG_DISABLE_PLUGIN_KEY, !newValue);
    });
    vbox.getChildren().add(displayDialog);
    layout.setBody(vbox);

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(cancelEvent -> {
      this.close();
    });

    Button disableButton = new Button("Disable Plugin");
    disableButton.setOnAction(removeEvent -> {
      this.disablePluginWithoutPrompt(plugin);
      this.close();
    });

    layout.setActions(disableButton, cancelButton);
    return layout;
  }

  public void setPlugin(Plugin plugin) {
    this.plugin = plugin;
  }

  public void disablePluginWithoutPrompt(Plugin plugin) {
    pluginService.disablePlugin(plugin);
    pluginsController.refresh();
    pluginInfoController.refresh();
  }
}
