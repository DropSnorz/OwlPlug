package com.dropsnorz.owlplug.controllers;

import java.io.File;

import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginDirectory;
import com.dropsnorz.owlplug.utils.PlatformUtils;
import com.jfoenix.controls.JFXButton;
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

	@FXML
	Label directoryPathLabel;
	
	@FXML
	JFXListView<Plugin> pluginDirectoryListView;
	
	@FXML 
	JFXButton openDirectoryButton;
	
	private Image brickImage  = new Image(getClass().getResourceAsStream("/icons/soundwave-blue-16.png"));;
	private Image directoryImage = new Image(getClass().getResourceAsStream("/icons/folder-grey-16.png"));

	
	@FXML
	public void initialize() { 
		
		openDirectoryButton.setGraphic(new ImageView(directoryImage));
		openDirectoryButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				PlatformUtils.openDirectoryExplorer(directoryPathLabel.getText());
			};
		});
		
		pluginDirectoryListView.setCellFactory(param -> new JFXListCell<Plugin>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(Plugin plug, boolean empty) {
                super.updateItem(plug, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setImage(brickImage);
                    setText(plug.getName());
                    setGraphic(imageView);
                }
            }
        });

	}
	
	
	public void setPluginDirectory(PluginDirectory pluginDirectory){
		
		directoryPathLabel.setText(pluginDirectory.getPath());
		pluginDirectoryListView.getItems().setAll(pluginDirectory.getPluginList());
	}

}

