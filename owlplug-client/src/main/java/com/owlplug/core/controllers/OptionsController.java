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

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.model.platform.OperatingSystem;
import com.owlplug.core.services.NativeHostService;
import com.owlplug.core.services.OptionsService;
import com.owlplug.core.utils.PlatformUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

@Controller
public class OptionsController extends BaseController {

  @Autowired
  private OptionsService optionsService;
  @Autowired
  private NativeHostService nativeHostService;

  @FXML
  private JFXToggleButton vst2ToggleButton;
  @FXML
  private JFXTextField vst2DirectoryTextField;
  @FXML
  private JFXButton vst2DirectoryButton;
  @FXML
  private JFXToggleButton vst3ToggleButton;
  @FXML
  private JFXToggleButton auToggleButton;
  @FXML
  private JFXTextField vst3DirectoryTextField;
  @FXML
  private JFXButton vst3DirectoryButton;
  @FXML
  private JFXTextField auDirectoryTextField;
  @FXML
  private JFXButton auDirectoryButton;
  @FXML
  private JFXCheckBox pluginNativeCheckbox;

  @FXML
  private JFXCheckBox syncPluginsCheckBox;
  @FXML
  private JFXButton removeDataButton;
  @FXML
  private Label versionLabel;

  @FXML
  private JFXButton clearCacheButton;
  @FXML
  private JFXCheckBox storeSubDirectoryCheckBox;
  @FXML
  private JFXCheckBox storeByCreatorCheckBox;
  @FXML
  private Label storeByCreatorLabel;
  @FXML
  private Label warningSubDirectory;
  @FXML
  private JFXCheckBox storeDirectoryCheckBox;
  @FXML
  private JFXTextField storeDirectoryTextField;
  @FXML
  private Label storeDirectorySeperator;
  @FXML
  private Hyperlink owlplugWebsiteLink;

  /**
   * FXML initialize method.
   */
  @FXML
  public void initialize() {

    vst2DirectoryTextField.setDisable(false);
    vst2DirectoryButton.setDisable(false);
    storeByCreatorLabel.setVisible(false);

    vst2ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().putBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, newValue);
      vst2DirectoryTextField.setDisable(!newValue);
      vst2DirectoryButton.setDisable(!newValue);
    });

    vst3ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().putBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, newValue);
      vst3DirectoryTextField.setDisable(!newValue);
      vst3DirectoryButton.setDisable(!newValue);
    });

    auToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().putBoolean(ApplicationDefaults.AU_DISCOVERY_ENABLED_KEY, newValue);
      auDirectoryTextField.setDisable(!newValue);
      auDirectoryButton.setDisable(!newValue);
    });

    vst2DirectoryButton.setOnAction(e -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      Window mainWindow = vst2DirectoryButton.getScene().getWindow();
      File selectedDirectory = directoryChooser.showDialog(mainWindow);
      if (selectedDirectory != null) {
        vst2DirectoryTextField.setText(selectedDirectory.getAbsolutePath());
      }
    });

    vst2DirectoryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().put(ApplicationDefaults.VST_DIRECTORY_KEY, newValue);
    });

    vst3DirectoryButton.setOnAction(e -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      Window mainWindow = vst3DirectoryButton.getScene().getWindow();
      File selectedDirectory = directoryChooser.showDialog(mainWindow);
      if (selectedDirectory != null) {
        vst3DirectoryTextField.setText(selectedDirectory.getAbsolutePath());
      }
    });

    vst3DirectoryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().put(ApplicationDefaults.VST3_DIRECTORY_KEY, newValue);
    });

    auDirectoryButton.setOnAction(e -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      Window mainWindow = auDirectoryButton.getScene().getWindow();
      File selectedDirectory = directoryChooser.showDialog(mainWindow);
      if (selectedDirectory != null) {
        auDirectoryTextField.setText(selectedDirectory.getAbsolutePath());
      }
    });

    auDirectoryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().put(ApplicationDefaults.AU_DIRECTORY_KEY, newValue);
    });

    pluginNativeCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().putBoolean(ApplicationDefaults.NATIVE_HOST_ENABLED_KEY, newValue);
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
      double width = newValue ? 150 : 0;
      storeDirectoryTextField.setVisible(newValue);
      storeDirectorySeperator.setVisible(newValue);
      storeDirectoryTextField.setDisable(!newValue);
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
      JFXDialog dialog = this.getDialogController().newDialog();
      JFXDialogLayout layout = new JFXDialogLayout();
      layout.setHeading(new Label("Remove plugin"));
      layout.setBody(new Label("Do you really want to remove all user data including accounts, "
          + "stores and custom settings ?"));

      JFXButton cancelButton = new JFXButton("Cancel");
      cancelButton.setOnAction(cancelEvent -> {
        dialog.close();
      });

      JFXButton removeButton = new JFXButton("Clear");
      removeButton.setOnAction(removeEvent -> {
        dialog.close();
        optionsService.clearAllUserData();
      });
      removeButton.getStyleClass().add("button-danger");

      layout.setActions(removeButton, cancelButton);
      dialog.setContent(layout);
      dialog.show();
    });

    versionLabel.setText("V " + this.getApplicationDefaults().getVersion());

    owlplugWebsiteLink.setOnAction(e -> {
      PlatformUtils.openDefaultBrowser(owlplugWebsiteLink.getText());
    });

    refreshView();
  }

  public void refreshView() {

    vst2DirectoryTextField.setText(this.getPreferences().get(ApplicationDefaults.VST_DIRECTORY_KEY, ""));
    vst3DirectoryTextField.setText(this.getPreferences().get(ApplicationDefaults.VST3_DIRECTORY_KEY, ""));
    auDirectoryTextField.setText(this.getPreferences().get(ApplicationDefaults.AU_DIRECTORY_KEY, ""));
    vst2ToggleButton.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false));
    vst3ToggleButton.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false));
    auToggleButton.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.AU_DISCOVERY_ENABLED_KEY, false));
    pluginNativeCheckbox.setDisable(!nativeHostService.isNativeHostAvailable());
    pluginNativeCheckbox.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.NATIVE_HOST_ENABLED_KEY, false));
    syncPluginsCheckBox.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.SYNC_PLUGINS_STARTUP_KEY, false));
    storeSubDirectoryCheckBox.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, true));
    warningSubDirectory.setVisible(!this.getPreferences().getBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, true));
    storeDirectoryCheckBox.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, false));
    storeByCreatorCheckBox.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.STORE_BY_CREATOR_ENABLED_KEY, false));
    storeDirectoryTextField.setText(this.getPreferences().get(ApplicationDefaults.STORE_DIRECTORY_KEY, ""));

    if (!storeDirectoryCheckBox.isSelected()) {
      storeDirectoryTextField.setDisable(true);
      storeDirectoryTextField.setVisible(false);
    }
    if (!storeByCreatorCheckBox.isSelected()) {
      storeByCreatorLabel.setVisible(false);
    }
    
    // Disable AU options for non MAC users
    if(!this.getApplicationDefaults().getRuntimePlatform()
    		.getOperatingSystem().equals(OperatingSystem.MAC)) {
    	auToggleButton.setSelected(false);
    	auToggleButton.setDisable(true);
    }
    
  }

}
