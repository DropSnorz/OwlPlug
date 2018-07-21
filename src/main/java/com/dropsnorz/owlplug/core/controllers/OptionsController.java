package com.dropsnorz.owlplug.core.controllers;

import java.io.File;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.core.services.OptionsService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

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
	private JFXTextField pluginDirectoryTextField;
	@FXML
	private JFXButton pluginDirectoryButton;
	@FXML
	private JFXToggleButton vst3ToggleButton;
	@FXML
	private JFXCheckBox syncPluginsCheckBox;
	@FXML
	private JFXButton removeDataButton;
	@FXML
	private Label versionLabel;
	@FXML
	private JFXCheckBox storeDirectoryCheckBox;
	@FXML
	private JFXTextField storeDirectoryTextField;
	@FXML
	private JFXButton storeDirectoryButton;


	@FXML
	public void initialize() {

		pluginDirectoryTextField.setDisable(false);
		pluginDirectoryButton.setDisable(false);

		vst2ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {

			prefs.putBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, newValue);
		});

		vst3ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) ->{

			prefs.putBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, newValue);
		});

		pluginDirectoryButton.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			Window mainWindow = pluginDirectoryButton.getScene().getWindow();

			File selectedDirectory = 
					directoryChooser.showDialog(mainWindow);

			if(selectedDirectory != null) {
				pluginDirectoryTextField.setText(selectedDirectory.getAbsolutePath());
			}
		});

		pluginDirectoryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			prefs.put(ApplicationDefaults.VST_DIRECTORY_KEY, newValue);
		});

		syncPluginsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			prefs.putBoolean(ApplicationDefaults.SYNC_PLUGINS_STARTUP_KEY, newValue);
		});


		removeDataButton.setOnAction(e -> {

			JFXDialog dialog = dialogController.newDialog();

			JFXDialogLayout layout = new JFXDialogLayout();

			layout.setHeading(new Label("Remove plugin"));
			layout.setBody(new Label("Do you really want to remove all user data including accounts, repositories and custom settings ?"));

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

		versionLabel.setText("OwlPlug - V" + applicationDefaults.getVersion());

		storeDirectoryCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			prefs.putBoolean(ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, newValue);
			storeDirectoryTextField.setDisable(!newValue);
			storeDirectoryButton.setDisable(!newValue);
		});


		storeDirectoryButton.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			Window mainWindow = storeDirectoryButton.getScene().getWindow();

			File selectedDirectory = 
					directoryChooser.showDialog(mainWindow);

			if(selectedDirectory != null){
				storeDirectoryTextField.setText(selectedDirectory.getAbsolutePath());
			}
		});

		storeDirectoryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			prefs.put(ApplicationDefaults.STORE_DIRECTORY_KEY, newValue);
		});


		refreshView();
	}

	public void refreshView() {

		pluginDirectoryTextField.setText(prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, ""));
		vst2ToggleButton.setSelected(prefs.getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false));
		vst3ToggleButton.setSelected(prefs.getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false));
		syncPluginsCheckBox.setSelected(prefs.getBoolean(ApplicationDefaults.SYNC_PLUGINS_STARTUP_KEY, false));
		storeDirectoryCheckBox.setSelected(prefs.getBoolean(ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, false));
		storeDirectoryTextField.setText(prefs.get(ApplicationDefaults.STORE_DIRECTORY_KEY, ""));
		
		if(!storeDirectoryCheckBox.isSelected()) {
			storeDirectoryTextField.setDisable(true);
			storeDirectoryButton.setDisable(false);
		}

	}

}
