package com.dropsnorz.owlplug.core.controllers.menus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
import com.dropsnorz.owlplug.core.controllers.MainController;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.core.controllers.dialogs.FileSystemRepositoryController;
import com.dropsnorz.owlplug.core.controllers.dialogs.GoogleDriveRepositoryController;
import com.jfoenix.controls.JFXDialog;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

@Controller
public class NewRepositoryController {
	
	@Autowired
	MainController mainController;
	
	@Autowired
	FileSystemRepositoryController fileSystemRepositoryController;
	
	@Autowired
	GoogleDriveRepositoryController googleDriveRepositoryController;
	
	@Autowired
	LazyViewRegistry viewRegistry;
	
	@FXML
	HBox fileSystemMenuItem;
	
	@FXML
	HBox googleDriveMenuItem;
	
	public void initialize() {
		
		fileSystemMenuItem.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				fileSystemRepositoryController.show();
				fileSystemRepositoryController.startCreateSequence();
				mainController.getLeftDrawer().close();
			}
		});
		
		googleDriveMenuItem.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				googleDriveRepositoryController.show();
				googleDriveRepositoryController.startCreateSequence();
				mainController.getLeftDrawer().close();
			}
		});
		
	}

}
