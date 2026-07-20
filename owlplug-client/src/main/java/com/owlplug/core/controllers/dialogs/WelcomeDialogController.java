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

import atlantafx.base.controls.ToggleSwitch;
import com.owlplug.controls.DialogLayout;
import com.owlplug.core.components.ApplicationDefaults.Prefs;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.SettingsController;
import com.owlplug.core.model.OperatingSystem;
import com.owlplug.plugin.components.PluginTaskFactory;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.ui.PluginFormatBadgeView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class WelcomeDialogController extends AbstractDialogController {

  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @Autowired
  private PluginTaskFactory taskFactory;
  @Autowired
  private SettingsController settingsController;

  @FXML
  private VBox stepWelcome;
  @FXML
  private VBox stepFormats;
  @FXML
  private VBox formatTogglesContainer;
  @FXML
  private Button startButton;
  @FXML
  private Button cancelButton;
  @FXML
  private Button backButton;
  @FXML
  private Button skipButton;
  @FXML
  private Button okButton;

  WelcomeDialogController() {
    super(640, 480);
    this.setOverlayClose(false);
  }

  /**
   * FXML initialize.
   */
  @FXML
  public void initialize() {
    startButton.setOnAction(e -> showStep(2));
    cancelButton.setOnAction(e -> this.close());
    backButton.setOnAction(e -> showStep(1));
    skipButton.setOnAction(e -> this.close());
    okButton.setOnAction(e -> {
      this.close();
      settingsController.refreshView();
      taskFactory.createPluginScanTask().schedule();
    });

    buildFormatToggles();
  }

  private void buildFormatToggles() {
    formatTogglesContainer.getChildren().addAll(
        buildFormatRow(PluginFormat.VST2, Prefs.Plugins.VST2_DISCOVERY_ENABLED,
            "VST2", "Legacy format, broadly supported across DAWs"),
        buildFormatRow(PluginFormat.VST3, Prefs.Plugins.VST3_DISCOVERY_ENABLED,
            "VST3", "Modern Steinberg format — recommended"),
        buildFormatRow(PluginFormat.AU, Prefs.Plugins.AU_DISCOVERY_ENABLED,
            "AU", "Audio Units — macOS only"),
        buildFormatRow(PluginFormat.LV2, Prefs.Plugins.LV2_DISCOVERY_ENABLED,
            "LV2", "Open standard, primarily for Linux")
    );

    // Disable AU options for non MAC users
    if (!this.getApplicationDefaults().getRuntimePlatform()
        .getOperatingSystem().equals(OperatingSystem.MAC)) {
      formatTogglesContainer.getChildren().get(2).setDisable(true);
    }
  }

  private HBox buildFormatRow(PluginFormat format, String enableKey, String name, String desc) {
    HBox row = new HBox(14);
    row.setAlignment(Pos.CENTER_LEFT);
    row.setPadding(new Insets(10, 4, 10, 4));

    PluginFormatBadgeView badge = new PluginFormatBadgeView(
        format, this.getApplicationDefaults(), PluginFormatBadgeView.DisplayMode.DEFAULT);

    VBox labels = new VBox(2);
    Label nameLabel = new Label(name);
    nameLabel.getStyleClass().add("label-emphase");
    Label descLabel = new Label(desc);
    descLabel.getStyleClass().add("label-disabled");
    labels.getChildren().addAll(nameLabel, descLabel);
    HBox.setHgrow(labels, Priority.ALWAYS);

    ToggleSwitch toggle = new ToggleSwitch();
    toggle.setSelected(this.getPreferences().getBoolean(enableKey, false));
    toggle.selectedProperty().addListener((obs, old, v) ->
        this.getPreferences().putBoolean(enableKey, v));

    row.getChildren().addAll(badge, labels, toggle);
    return row;
  }

  private void showStep(int step) {
    boolean onStep1 = step == 1;
    stepWelcome.setVisible(onStep1);
    stepWelcome.setManaged(onStep1);
    stepFormats.setVisible(!onStep1);
    stepFormats.setManaged(!onStep1);
  }

  @Override
  protected DialogLayout getLayout() {
    DialogLayout layout = new DialogLayout();
    layout.setBody(lazyViewRegistry.get(LazyViewRegistry.WELCOME_VIEW));
    return layout;
  }

}