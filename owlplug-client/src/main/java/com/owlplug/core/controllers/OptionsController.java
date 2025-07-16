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

import com.owlplug.controls.Dialog;
import com.owlplug.controls.DialogLayout;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.controllers.dialogs.DonateDialogController;
import com.owlplug.core.controllers.dialogs.ListDirectoryDialogController;
import com.owlplug.core.controllers.fragments.PluginPathFragmentController;
import com.owlplug.core.model.platform.OperatingSystem;
import com.owlplug.core.services.NativeHostService;
import com.owlplug.core.services.OptionsService;
import com.owlplug.core.ui.SlidingLabel;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.host.loaders.NativePluginLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class OptionsController extends BaseController {

  @Autowired
  private OptionsService optionsService;
  @Autowired
  private NativeHostService nativeHostService;
  @Autowired
  private ListDirectoryDialogController listDirectoryDialogController;
  @Autowired
  private DonateDialogController donateDialogController;
  @FXML
  private CheckBox pluginNativeCheckbox;
  @FXML
  private ComboBox<NativePluginLoader> pluginNativeComboBox;
  @FXML
  private CheckBox syncPluginsCheckBox;
  @FXML
  private Button removeDataButton;
  @FXML
  private Label versionLabel;

  @FXML
  private Button clearCacheButton;
  @FXML
  private CheckBox storeSubDirectoryCheckBox;
  @FXML
  private CheckBox storeByCreatorCheckBox;
  @FXML
  private Label storeByCreatorLabel;
  @FXML
  private Label warningSubDirectory;
  @FXML
  private CheckBox storeDirectoryCheckBox;
  @FXML
  private TextField storeDirectoryTextField;
  @FXML
  private Label storeDirectorySeparator;
  @FXML
  private Hyperlink owlplugWebsiteLink;
  @FXML
  private VBox pluginPathContainer;
  @FXML
  private Button moreFeaturesButton;
  @FXML
  private TextFlow versionTextFlow;

  private PluginPathFragmentController vst2PluginPathFragment;
  private PluginPathFragmentController vst3PluginPathFragment;
  private PluginPathFragmentController auPluginPathFragment;
  private PluginPathFragmentController lv2PluginPathFragment;

  /**
   * FXML initialize method.
   */
  @FXML
  public void initialize() {

    vst2PluginPathFragment = new PluginPathFragmentController("VST2",
      ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY,
      ApplicationDefaults.VST_DIRECTORY_KEY,
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

    storeByCreatorLabel.setVisible(false);

    pluginNativeCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().putBoolean(ApplicationDefaults.NATIVE_HOST_ENABLED_KEY, newValue);
      this.pluginNativeComboBox.setDisable(!newValue);
    });

    ObservableList<NativePluginLoader> pluginLoaders = FXCollections.observableArrayList(
        nativeHostService.getAvailablePluginLoaders());
    pluginNativeComboBox.setItems(pluginLoaders);

    pluginNativeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        this.getPreferences().put(ApplicationDefaults.PREFERRED_NATIVE_LOADER,newValue.getId());
        nativeHostService.setCurrentPluginLoader(newValue);
      }
    });

    syncPluginsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().putBoolean(ApplicationDefaults.SYNC_PLUGINS_STARTUP_KEY, newValue);
    });

    storeSubDirectoryCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().putBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, newValue);
      warningSubDirectory.setVisible(!newValue);
    });

    warningSubDirectory.managedProperty().bind(warningSubDirectory.visibleProperty());

    storeDirectoryCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().putBoolean(ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, newValue);
      storeDirectoryTextField.setVisible(newValue);
      storeDirectorySeparator.setVisible(newValue);
      storeDirectoryTextField.setDisable(!newValue);
      double width = newValue ? 150 : 0;
      storeDirectoryTextField.setMaxWidth(width);
    });

    storeByCreatorCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().putBoolean(ApplicationDefaults.STORE_BY_CREATOR_ENABLED_KEY, newValue);
      storeByCreatorLabel.setVisible(newValue);
    });

    storeDirectoryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().put(ApplicationDefaults.STORE_DIRECTORY_KEY, newValue);
    });

    clearCacheButton.setOnAction(e -> {
      optionsService.clearCache();
    });

    removeDataButton.setOnAction(e -> {
      Dialog dialog = this.getDialogManager().newDialog();
      DialogLayout layout = new DialogLayout();
      layout.setHeading(new Label("Remove user data"));
      layout.setBody(new Label("Do you really want to remove all user data including accounts, "
          + "stores and custom settings ? \n\nYou must restart OwlPlug for a complete reset."));

      Button cancelButton = new Button("Cancel");
      cancelButton.setOnAction(cancelEvent -> {
        dialog.close();
      });

      Button removeButton = new Button("Remove data");
      removeButton.setOnAction(removeEvent -> {
        dialog.close();
        optionsService.clearAllUserData();
        this.refreshView();

        // User data cleared twice because the refreshView() triggers UI changes that may be replicated in data
        optionsService.clearAllUserData();

      });
      removeButton.getStyleClass().add("button-danger");

      layout.setActions(removeButton, cancelButton);
      dialog.setContent(layout);
      dialog.show();
    });

    versionLabel.setText(this.getApplicationDefaults().getVersion());

    owlplugWebsiteLink.setOnAction(e -> {
      PlatformUtils.openDefaultBrowser(owlplugWebsiteLink.getText());
    });

    moreFeaturesButton.setOnAction(e -> {
      donateDialogController.show();
    });

    versionTextFlow.getChildren().add(new SlidingLabel(ApplicationDefaults.getContributors()));

    refreshView();
  }

  public void refreshView() {

    vst2PluginPathFragment.refresh();
    vst3PluginPathFragment.refresh();
    auPluginPathFragment.refresh();
    lv2PluginPathFragment.refresh();

    pluginNativeCheckbox.setDisable(!nativeHostService.isNativeHostAvailable());
    pluginNativeComboBox.setDisable(!nativeHostService.isNativeHostAvailable());
    pluginNativeCheckbox.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.NATIVE_HOST_ENABLED_KEY, false));
    syncPluginsCheckBox.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.SYNC_PLUGINS_STARTUP_KEY, false));
    storeSubDirectoryCheckBox.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, true));
    warningSubDirectory.setVisible(!this.getPreferences().getBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, true));
    storeDirectoryCheckBox.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, false));
    storeByCreatorCheckBox.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.STORE_BY_CREATOR_ENABLED_KEY, false));
    storeDirectoryTextField.setText(this.getPreferences().get(ApplicationDefaults.STORE_DIRECTORY_KEY, ""));

    NativePluginLoader pluginLoader = nativeHostService.getCurrentPluginLoader();
    pluginNativeComboBox.getSelectionModel().select(pluginLoader);

    if (!storeDirectoryCheckBox.isSelected()) {
      storeDirectoryTextField.setDisable(true);
      storeDirectoryTextField.setVisible(false);
    }
    if (!storeByCreatorCheckBox.isSelected()) {
      storeByCreatorLabel.setVisible(false);
    }
    
    // Disable AU options for non MAC users
    if (!this.getApplicationDefaults().getRuntimePlatform()
        .getOperatingSystem().equals(OperatingSystem.MAC)) {
      auPluginPathFragment.disable();
    }

  }

}
