package com.dropsnorz.owlplug.core.controllers.menus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
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
	
	@Autowired
	LazyViewRegistry viewRegistry;
	
	@FXML
	HBox fileSystemMenuItem;
	
	public void initialize() {
		
		fileSystemMenuItem.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				
				JFXDialog dialog = dialogController.newBigDialog(viewRegistry.getAsNode(LazyViewRegistry.NEW_FILESYSTEM_REPOSITORY_VIEW));
				dialog.show();
				
				mainController.getLeftDrawer().close();
				
			}
			
		});
		
	}

}
