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
 
package com.owlplug.core.controllers.dialogs;

import com.owlplug.controls.DialogLayout;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.OptionsController;
import com.owlplug.core.controllers.fragments.PluginPathFragmentController;
import com.owlplug.core.model.platform.OperatingSystem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class WelcomeDialogController extends AbstractDialogController {

  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @Autowired
  private CoreTaskFactory taskFactory;
  @Autowired
  private OptionsController optionsController;
  @Autowired
  private ListDirectoryDialogController listDirectoryDialogController;

  @FXML
  private VBox pluginPathContainer;
  @FXML
  private Button okButton;
  @FXML
  private Button cancelButton;

  private PluginPathFragmentController vst2PluginPathFragment;
  private PluginPathFragmentController vst3PluginPathFragment;
  private PluginPathFragmentController auPluginPathFragment;
  private PluginPathFragmentController lv2PluginPathFragment;

  WelcomeDialogController() {
    super(700, 300);
    this.setOverlayClose(false);
  }

  /**
   * FXML initialize.
   */
  public void initialize() {

    vst2PluginPathFragment = new PluginPathFragmentController("VST2",
      ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, ApplicationDefaults.VST_DIRECTORY_KEY,
      ApplicationDefaults.VST2_EXTRA_DIRECTORY_KEY,
      this.getPreferences(),
      this.listDirectoryDialogController);
    vst3PluginPathFragment = new PluginPathFragmentController("VST3",
      ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY,
      ApplicationDefaults.VST3_DIRECTORY_KEY,
      ApplicationDefaults.VST3_EXTRA_DIRECTORY_KEY,
      this.getPreferences(),
      this.listDirectoryDialogController);
    auPluginPathFragment = new PluginPathFragmentController("AU",
      ApplicationDefaults.AU_DISCOVERY_ENABLED_KEY,
      ApplicationDefaults.AU_DIRECTORY_KEY,
      ApplicationDefaults.AU_EXTRA_DIRECTORY_KEY,
      this.getPreferences(),
      this.listDirectoryDialogController);
    lv2PluginPathFragment = new PluginPathFragmentController("LV2",
      ApplicationDefaults.LV2_DISCOVERY_ENABLED_KEY,
      ApplicationDefaults.LV2_DIRECTORY_KEY,
      ApplicationDefaults.LV2_EXTRA_DIRECTORY_KEY,
      this.getPreferences(),
      this.listDirectoryDialogController);

    pluginPathContainer.getChildren().add(vst2PluginPathFragment.getNode());
    pluginPathContainer.getChildren().add(vst3PluginPathFragment.getNode());
    pluginPathContainer.getChildren().add(auPluginPathFragment.getNode());
    pluginPathContainer.getChildren().add(lv2PluginPathFragment.getNode());

    okButton.setOnAction(e -> {
      this.close();
      optionsController.refreshView();
      taskFactory.createPluginSyncTask().schedule();
    });

    cancelButton.setOnAction(e -> {
      this.close();
    });

    refreshView();

  }

  public void refreshView() {
    vst2PluginPathFragment.refresh();
    vst3PluginPathFragment.refresh();
    auPluginPathFragment.refresh();
    lv2PluginPathFragment.refresh();

    // Disable AU options for non MAC users
    if (!this.getApplicationDefaults().getRuntimePlatform()
        .getOperatingSystem().equals(OperatingSystem.MAC)) {
      auPluginPathFragment.disable();
    }

  }

  @Override
  protected void onDialogShow() {
    this.refreshView();
  }

  @Override
  protected DialogLayout getLayout() {
    Label title = new Label("Owlplug is almost ready !");
    title.getStyleClass().add("heading-3");
    ImageView iv = new ImageView(this.getApplicationDefaults().rocketImage);
    iv.setFitHeight(20);
    iv.setFitWidth(20);
    title.setGraphic(iv);
    DialogLayout layout = new DialogLayout();
    layout.setHeading(title);
    layout.setBody(lazyViewRegistry.get(LazyViewRegistry.WELCOME_VIEW));

    return layout;
  }

}
