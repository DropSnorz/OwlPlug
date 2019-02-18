package com.owlplug.core.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.controllers.dialogs.DialogController;
import com.owlplug.core.services.OptionsService;
import java.io.File;
import java.util.prefs.Preferences;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class OptionsController {

  @Autowired
  private Preferences prefs;
  @Autowired
  private ApplicationDefaults applicationDefaults;
  @Autowired
  private OptionsService optionsService;
  @Autowired
  private DialogController dialogController;

  @FXML
  private JFXToggleButton vst2ToggleButton;
  @FXML
  private JFXTextField vst2DirectoryTextField;
  @FXML
  private JFXButton vst2DirectoryButton;
  @FXML
  private JFXToggleButton vst3ToggleButton;
  @FXML
  private JFXTextField vst3DirectoryTextField;
  @FXML
  private JFXButton vst3DirectoryButton;

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
  private Label warningSubDirectory;
  @FXML
  private JFXCheckBox storeDirectoryCheckBox;
  @FXML
  private JFXTextField storeDirectoryTextField;

  /**
   * FXML initialize method.
   */
  @FXML
  public void initialize() {

    vst2DirectoryTextField.setDisable(false);
    vst2DirectoryButton.setDisable(false);

    vst2ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
      prefs.putBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, newValue);
      vst2DirectoryTextField.setDisable(!newValue);
      vst2DirectoryButton.setDisable(!newValue);
    });

    vst3ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
      prefs.putBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, newValue);
      vst3DirectoryTextField.setDisable(!newValue);
      vst3DirectoryButton.setDisable(!newValue);
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
      prefs.put(ApplicationDefaults.VST_DIRECTORY_KEY, newValue);
    });

    vst3DirectoryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      prefs.put(ApplicationDefaults.VST3_DIRECTORY_KEY, newValue);
    });


    syncPluginsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      prefs.putBoolean(ApplicationDefaults.SYNC_PLUGINS_STARTUP_KEY, newValue);
    });

    storeSubDirectoryCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      prefs.putBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, newValue);
      warningSubDirectory.setVisible(!newValue);
    });

    warningSubDirectory.managedProperty().bind(warningSubDirectory.visibleProperty());

    storeDirectoryCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      prefs.putBoolean(ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, newValue);
      storeDirectoryTextField.setDisable(!newValue);
    });

    storeDirectoryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      prefs.put(ApplicationDefaults.STORE_DIRECTORY_KEY, newValue);
    });

    clearCacheButton.setOnAction(e -> {
      optionsService.clearCache();
    });

    removeDataButton.setOnAction(e -> {

      JFXDialog dialog = dialogController.newDialog();

      JFXDialogLayout layout = new JFXDialogLayout();

      layout.setHeading(new Label("Remove plugin"));
      layout.setBody(new Label(
          "Do you really want to remove all user data including accounts, " + "repositories and custom settings ?"));

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

    versionLabel.setText("V " + applicationDefaults.getVersion());

    refreshView();
  }

  public void refreshView() {

    vst2DirectoryTextField.setText(prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, ""));
    vst3DirectoryTextField.setText(prefs.get(ApplicationDefaults.VST3_DIRECTORY_KEY, ""));
    vst2ToggleButton.setSelected(prefs.getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false));
    vst3ToggleButton.setSelected(prefs.getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false));
    syncPluginsCheckBox.setSelected(prefs.getBoolean(ApplicationDefaults.SYNC_PLUGINS_STARTUP_KEY, false));
    storeSubDirectoryCheckBox.setSelected(prefs.getBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, true));
    warningSubDirectory.setVisible(!prefs.getBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, true));
    storeDirectoryCheckBox.setSelected(prefs.getBoolean(ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, false));
    storeDirectoryTextField.setText(prefs.get(ApplicationDefaults.STORE_DIRECTORY_KEY, ""));

    if (!storeDirectoryCheckBox.isSelected()) {
      storeDirectoryTextField.setDisable(true);
    }

  }

}
