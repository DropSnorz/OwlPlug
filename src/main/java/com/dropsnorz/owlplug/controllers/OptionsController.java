package com.dropsnorz.owlplug.controllers;

import java.io.File;

import org.springframework.stereotype.Controller;

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

	@FXML
	JFXToggleButton vst2ToggleButton;
	@FXML
	JFXTextField vst2DirectoryTextField;
	@FXML
	JFXButton vst2DirectoryButton;
	@FXML
	JFXToggleButton vst3ToggleButton;
	@FXML
	JFXTextField vst3DirectoryTextField;
	@FXML
	JFXButton vst3DirectoryButton;

	@FXML
	public void initialize() {

		vst2ToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if(newValue == true){
					vst2DirectoryTextField.setDisable(false);
					vst2DirectoryButton.setDisable(false);
				}
				else{
					vst2DirectoryTextField.setDisable(true);
					vst2DirectoryButton.setDisable(true);
				}
			}

		});

		vst3ToggleButton.selectedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if(newValue == true){
					vst3DirectoryTextField.setDisable(false);
					vst3DirectoryButton.setDisable(false);
				}
				else{
					vst3DirectoryTextField.setDisable(true);
					vst3DirectoryButton.setDisable(true);
				}
			}

		});

		vst2DirectoryButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				Window mainWindow = vst2DirectoryButton.getScene().getWindow();

				File selectedDirectory = 
						directoryChooser.showDialog(mainWindow);

				if(selectedDirectory == null){
					vst2DirectoryTextField.setText("");
				}else{
					vst2DirectoryTextField.setText(selectedDirectory.getAbsolutePath());
				}
			}
		});

		vst3DirectoryButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				Window mainWindow = vst3DirectoryButton.getScene().getWindow();

				File selectedDirectory = 
						directoryChooser.showDialog(mainWindow);

				if(selectedDirectory == null){
					vst3DirectoryTextField.setText("");
				}else{
					vst3DirectoryTextField.setText(selectedDirectory.getAbsolutePath());
				}
			}
		});


	}

}
