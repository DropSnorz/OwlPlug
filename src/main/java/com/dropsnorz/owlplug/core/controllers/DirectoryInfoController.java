package com.dropsnorz.owlplug.core.controllers;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.model.PluginDirectory;
import com.dropsnorz.owlplug.core.services.TaskFactory;
import com.dropsnorz.owlplug.core.ui.PluginListCellFactory;
import com.dropsnorz.owlplug.core.utils.PlatformUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@Controller
public class DirectoryInfoController {

	@Autowired
	ApplicationDefaults applicationDefaults;
	
	@Autowired
	DialogController dialogController;
	
	@Autowired
	TaskFactory taskFactory;
	
	@FXML
	Label directoryPathLabel;
	
	@FXML
	JFXListView<Plugin> pluginDirectoryListView;
	
	@FXML 
	JFXButton openDirectoryButton;
	
	@FXML 
	JFXButton deleteDirectoryButton;
	
	private PluginDirectory pluginDirectory;

	
	@FXML
	public void initialize() { 
		
		openDirectoryButton.setGraphic(new ImageView(applicationDefaults.directoryImage));
		openDirectoryButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				PlatformUtils.openDirectoryExplorer(pluginDirectory.getPath());
			};
		});
		
		pluginDirectoryListView.setCellFactory(new PluginListCellFactory(applicationDefaults));
		
		deleteDirectoryButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				
				JFXDialog dialog = dialogController.newDialog();
				
				JFXDialogLayout layout = new JFXDialogLayout();
				
				layout.setHeading(new Label("Remove directory"));
				layout.setBody(new Label("Do you really want to remove " + pluginDirectory.getName()
				+ " and all of its content ? This will permanently delete the file from your hard drive."));
				
				JFXButton cancelButton = new JFXButton("Cancel");
				
				cancelButton.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {
						dialog.close();
					};
				});	
				
				JFXButton removeButton = new JFXButton("Remove");
				
				removeButton.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {
						dialog.close();
						taskFactory.run(taskFactory.createDirectoryRemoveTask(pluginDirectory));
					};
				});	
				removeButton.getStyleClass().add("button-danger");
				
				layout.setActions(removeButton, cancelButton);
				dialog.setContent(layout);
				dialog.show();
				
			};
		});

	}
	
	
	public void setPluginDirectory(PluginDirectory pluginDirectory){
		
		this.pluginDirectory = pluginDirectory;
		directoryPathLabel.setText(pluginDirectory.getPath());
		pluginDirectoryListView.getItems().setAll(pluginDirectory.getPluginList());
	}

}

