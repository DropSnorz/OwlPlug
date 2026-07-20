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
import com.owlplug.core.controllers.dialogs.ListDirectoryDialogController;
import com.owlplug.core.controllers.fragments.PluginPathFragmentController;
import com.owlplug.core.model.OperatingSystem;
import com.owlplug.host.loaders.NativePluginLoader;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.services.NativeHostService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PluginScanSettingsController extends BaseController {

  @Autowired
  private NativeHostService nativeHostService;
  @Autowired
  private ListDirectoryDialogController listDirectoryDialogController;

  @FXML
  private VBox formatsContainer;
  @FXML
  private CheckBox pluginNativeCheckbox;
  @FXML
  private ComboBox<NativePluginLoader> pluginNativeComboBox;
  @FXML
  private Spinner<Integer> loaderTimeoutSpinner;
  @FXML
  private CheckBox syncPluginsCheckBox;
  @FXML
  private CheckBox syncFileStatCheckbox;

  private PluginPathFragmentController vst2Fragment;
  private PluginPathFragmentController vst3Fragment;
  private PluginPathFragmentController auFragment;
  private PluginPathFragmentController lv2Fragment;

  @FXML
  public void initialize() {
    vst2Fragment = new PluginPathFragmentController(PluginFormat.VST2,
        Prefs.Plugins.VST2_DISCOVERY_ENABLED, Prefs.Plugins.VST2_DIRECTORY,
        Prefs.Plugins.VST2_EXTRA_DIRECTORY, getPreferences(),
        getApplicationDefaults(), listDirectoryDialogController);
    vst3Fragment = new PluginPathFragmentController(PluginFormat.VST3,
        Prefs.Plugins.VST3_DISCOVERY_ENABLED, Prefs.Plugins.VST3_DIRECTORY,
        Prefs.Plugins.VST3_EXTRA_DIRECTORY, getPreferences(),
        getApplicationDefaults(), listDirectoryDialogController);
    auFragment = new PluginPathFragmentController(PluginFormat.AU,
        Prefs.Plugins.AU_DISCOVERY_ENABLED, Prefs.Plugins.AU_DIRECTORY,
        Prefs.Plugins.AU_EXTRA_DIRECTORY, getPreferences(),
        getApplicationDefaults(), listDirectoryDialogController);
    lv2Fragment = new PluginPathFragmentController(PluginFormat.LV2,
        Prefs.Plugins.LV2_DISCOVERY_ENABLED, Prefs.Plugins.LV2_DIRECTORY,
        Prefs.Plugins.LV2_EXTRA_DIRECTORY, getPreferences(),
        getApplicationDefaults(), listDirectoryDialogController);

    formatsContainer.getChildren().addAll(
        vst2Fragment.getNode(),
        vst3Fragment.getNode(),
        auFragment.getNode(),
        lv2Fragment.getNode());

    pluginNativeCheckbox.selectedProperty().addListener((obs, o, n) -> {
      getPreferences().putBoolean(Prefs.Plugins.NativeHost.ENABLED, n);
      pluginNativeComboBox.setDisable(!n);
      updateScannerTimeoutFieldState();
    });

    pluginNativeComboBox.setItems(
        FXCollections.observableArrayList(nativeHostService.getAvailablePluginLoaders()));
    pluginNativeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
      if (n != null) {
        getPreferences().put(Prefs.Plugins.NativeHost.PREFERRED_LOADER, n.getId());
        nativeHostService.setCurrentPluginLoader(n);
        updateScannerTimeoutFieldState();
      }
    });

    loaderTimeoutSpinner.setValueFactory(
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 3600, 10));
    loaderTimeoutSpinner.valueProperty().addListener((obs, o, n) -> {
      getPreferences().putLong(Prefs.Plugins.NativeHost.LOADER_TIMEOUT, n.longValue());
      nativeHostService.setScannerTimeout(n.longValue());
    });

    syncPluginsCheckBox.selectedProperty().addListener((obs, o, n) ->
        getPreferences().putBoolean(Prefs.App.SYNC_PLUGINS_ON_STARTUP, n));
    syncFileStatCheckbox.selectedProperty().addListener((obs, o, n) ->
        getPreferences().putBoolean(Prefs.App.SYNC_FILE_STAT, n));
  }

  public void refresh() {
    vst2Fragment.refresh();
    vst3Fragment.refresh();
    auFragment.refresh();
    lv2Fragment.refresh();

    boolean nativeAvailable = nativeHostService.isNativeHostAvailable();
    pluginNativeCheckbox.setDisable(!nativeAvailable);
    pluginNativeComboBox.setDisable(!nativeAvailable);
    pluginNativeCheckbox.setSelected(
        getPreferences().getBoolean(Prefs.Plugins.NativeHost.ENABLED, false));

    NativePluginLoader loader = nativeHostService.getCurrentPluginLoader();
    pluginNativeComboBox.getSelectionModel().select(loader);

    int timeout = (int) getPreferences().getLong(Prefs.Plugins.NativeHost.LOADER_TIMEOUT, 10L);
    loaderTimeoutSpinner.getValueFactory().setValue(timeout);
    updateScannerTimeoutFieldState();

    syncPluginsCheckBox.setSelected(
        getPreferences().getBoolean(Prefs.App.SYNC_PLUGINS_ON_STARTUP, false));
    syncFileStatCheckbox.setSelected(
        getPreferences().getBoolean(Prefs.App.SYNC_FILE_STAT, true));

    if (!getApplicationDefaults().getRuntimePlatform()
        .getOperatingSystem().equals(OperatingSystem.MAC)) {
      auFragment.disable();
    }
  }

  private void updateScannerTimeoutFieldState() {
    NativePluginLoader selected = pluginNativeComboBox.getSelectionModel().getSelectedItem();
    boolean isOwlPlugScanner = selected != null && "owlplug-scanner".equals(selected.getId());
    boolean nativeEnabled = pluginNativeCheckbox.isSelected() && !pluginNativeCheckbox.isDisable();
    loaderTimeoutSpinner.setDisable(!nativeEnabled || !isOwlPlugScanner);
  }

}