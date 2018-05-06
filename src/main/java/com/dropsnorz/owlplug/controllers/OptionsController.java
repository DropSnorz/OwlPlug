package com.dropsnorz.owlplug.controllers;

import java.io.File;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

@Controller
public class OptionsController {
	
	@Autowired
	Preferences prefs;
	@FXML
	JFXToggleButton vst2ToggleButton;
	@FXML
	JFXTextField pluginDirectoryTextField;
	@FXML
	JFXButton pluginDirectoryButton;
	@FXML
	JFXToggleButton vst3ToggleButton;


	@FXML
	public void initialize() {
		
		pluginDirectoryTextField.setDisable(false);
		pluginDirectoryButton.setDisable(false);
		
		vst2ToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				
				prefs.putBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, newValue);
			}

		});

		vst3ToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				prefs.putBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, newValue);
			}

		});

		pluginDirectoryButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				Window mainWindow = pluginDirectoryButton.getScene().getWindow();

				File selectedDirectory = 
						directoryChooser.showDialog(mainWindow);

				if(selectedDirectory == null){
					pluginDirectoryTextField.setText("");
				}else{
					pluginDirectoryTextField.setText(selectedDirectory.getAbsolutePath());
				}
			}
		});

		
		
		pluginDirectoryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			prefs.put(ApplicationDefaults.VST_DIRECTORY_KEY, newValue);
		});

		refreshView();

	}
	
	public void refreshView() {
		
		pluginDirectoryTextField.setText(prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, ""));
		vst2ToggleButton.setSelected(prefs.getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false));
		vst3ToggleButton.setSelected(prefs.getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false));
		
	}

}
