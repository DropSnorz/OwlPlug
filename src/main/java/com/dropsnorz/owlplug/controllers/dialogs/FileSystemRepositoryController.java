package com.dropsnorz.owlplug.controllers.dialogs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.FileSystemRepository;
import com.dropsnorz.owlplug.services.PluginRepositoryService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;

@Controller
public class FileSystemRepositoryController extends DialogFrame {
	
	@Autowired
	PluginRepositoryService pluginRepositoryService;
	
	@FXML
	JFXButton closeButton;
	
	@FXML
	JFXButton addButton;
	
	@FXML
	JFXTextField repositoryNameTextField;
	
	@FXML
	JFXTextField repositoryPathTextField;
	
	
	public void initialize() {
		
		setOverlayClose(false);
		
		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				close();
			};
		});
		
		
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				
				String name = repositoryNameTextField.getText();
				String path = repositoryPathTextField.getText();
				
				FileSystemRepository repository = new FileSystemRepository(name, path);
				
				if (pluginRepositoryService.createRepository(repository)) {
					close();

				}
								
				
			};
		});
		
	}
	
	

}
