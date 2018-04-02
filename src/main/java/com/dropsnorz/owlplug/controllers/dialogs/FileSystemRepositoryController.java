package com.dropsnorz.owlplug.controllers.dialogs;

import org.springframework.stereotype.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;

@Controller
public class FileSystemRepositoryController extends DialogFrame {
	
	@FXML
	JFXButton closeButton;
	
	@FXML
	JFXButton addButton;
	
	
	public void initialize() {
		
		setOverlayClose(false);
		
		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				
				close();
			};
		});	
		
	}
	
	

}
