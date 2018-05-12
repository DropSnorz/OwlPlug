package com.dropsnorz.owlplug.core.controllers.menus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.core.controllers.MainController;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
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
	DialogController dialogController;
	
	@FXML
	HBox fileSystemMenuItem;
	
	public void initialize() {
		
		fileSystemMenuItem.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				
				JFXDialog dialog = dialogController.newBigDialog(( (Node) mainController.loadFxml("/fxml/dialogs/NewFileSystemRepository.fxml")));
				dialog.show();
				
				mainController.getLeftDrawer().close();
				
			}
			
		});
		
	}

}
