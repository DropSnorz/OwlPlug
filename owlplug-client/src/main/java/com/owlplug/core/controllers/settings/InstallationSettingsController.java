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

package com.owlplug.core.controllers.settings;

import com.owlplug.core.components.ApplicationDefaults.Prefs;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.plugin.ui.PluginFormatBadgeView;
import java.io.File;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;

@Controller
public class InstallationSettingsController extends BaseController {

  @FXML
  private CheckBox storeDirectoryCheckBox;
  @FXML
  private HBox storeDirectoryRow;
  @FXML
  private TextField storeDirectoryTextField;
  @FXML
  private CheckBox storeByCreatorCheckBox;
  @FXML
  private CheckBox storeSubDirectoryCheckBox;
  @FXML
  private Label warningSubDirectory;
  @FXML
  private VBox pathPreviewContainer;

  private Label vst2PathLabel;
  private Label vst3PathLabel;
  private Label auPathLabel;
  private Label lv2PathLabel;

  @FXML
  public void initialize() {
    storeDirectoryRow.visibleProperty().bind(storeDirectoryCheckBox.selectedProperty());
    storeDirectoryRow.managedProperty().bind(storeDirectoryCheckBox.selectedProperty());
    warningSubDirectory.managedProperty().bind(warningSubDirectory.visibleProperty());

    storeDirectoryCheckBox.selectedProperty().addListener((obs, o, n) -> {
      getPreferences().putBoolean(Prefs.Explore.STORE_DIRECTORY_ENABLED, n);
      refreshPathPreview();
    });
    storeByCreatorCheckBox.selectedProperty().addListener((obs, o, n) -> {
      getPreferences().putBoolean(Prefs.Explore.STORE_BY_CREATOR_ENABLED, n);
      refreshPathPreview();
    });
    storeSubDirectoryCheckBox.selectedProperty().addListener((obs, o, n) -> {
      getPreferences().putBoolean(Prefs.Explore.STORE_SUBDIRECTORY_ENABLED, n);
      warningSubDirectory.setVisible(!n);
      refreshPathPreview();
    });
    storeDirectoryTextField.textProperty().addListener((obs, o, n) -> {
      getPreferences().put(Prefs.Explore.STORE_DIRECTORY, n);
      refreshPathPreview();
    });

    vst2PathLabel = previewLabel();
    vst3PathLabel = previewLabel();
    auPathLabel   = previewLabel();
    lv2PathLabel  = previewLabel();

    pathPreviewContainer.getChildren().addAll(
        previewRow("vst2", vst2PathLabel),
        previewRow("vst3", vst3PathLabel),
        previewRow("au",   auPathLabel),
        previewRow("lv2",  lv2PathLabel));
  }

  public void refresh() {
    boolean storeSubDir = getPreferences().getBoolean(Prefs.Explore.STORE_SUBDIRECTORY_ENABLED, true);
    storeSubDirectoryCheckBox.setSelected(storeSubDir);
    warningSubDirectory.setVisible(!storeSubDir);
    storeDirectoryCheckBox.setSelected(
        getPreferences().getBoolean(Prefs.Explore.STORE_DIRECTORY_ENABLED, false));
    storeByCreatorCheckBox.setSelected(
        getPreferences().getBoolean(Prefs.Explore.STORE_BY_CREATOR_ENABLED, false));
    storeDirectoryTextField.setText(
        getPreferences().get(Prefs.Explore.STORE_DIRECTORY, ""));
    refreshPathPreview();
  }

  private void refreshPathPreview() {
    vst2PathLabel.setText(simulatePath(Prefs.Plugins.VST2_DIRECTORY));
    vst3PathLabel.setText(simulatePath(Prefs.Plugins.VST3_DIRECTORY));
    auPathLabel.setText(simulatePath(Prefs.Plugins.AU_DIRECTORY));
    lv2PathLabel.setText(simulatePath(Prefs.Plugins.LV2_DIRECTORY));
  }

  private String simulatePath(String formatDirKey) {
    String baseDir = getPreferences().get(formatDirKey, "");
    if (baseDir == null || baseDir.isBlank()) return "Format directory not configured";

    File path = new File(baseDir);
    if (storeDirectoryCheckBox.isSelected()) {
      String sub = storeDirectoryTextField.getText();
      if (sub != null && !sub.isBlank()) path = new File(path, sub);
    }
    if (storeByCreatorCheckBox.isSelected()) path = new File(path, "Acme Audio");
    if (storeSubDirectoryCheckBox.isSelected()) path = new File(path, "MyPlugin");
    return path.getAbsolutePath();
  }

  private Label previewLabel() {
    Label l = new Label();
    l.getStyleClass().add("label-disabled");
    l.setWrapText(true);
    HBox.setHgrow(l, Priority.ALWAYS);
    return l;
  }

  private HBox previewRow(String formatValue, Label pathLabel) {
    PluginFormatBadgeView badge = new PluginFormatBadgeView(
        formatValue, getApplicationDefaults(), PluginFormatBadgeView.DisplayMode.TEXT_ONLY);
    badge.setMinWidth(42);
    HBox row = new HBox(12, badge, pathLabel);
    row.setAlignment(Pos.CENTER_LEFT);
    return row;
  }

}