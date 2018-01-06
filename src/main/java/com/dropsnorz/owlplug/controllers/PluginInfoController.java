package com.dropsnorz.owlplug.controllers;

import java.io.File;

import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.utils.PlatformUtils;
import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@Controller
public class PluginInfoController {
	
	
	@FXML
	private ImageView pluginTypeIcon;
	@FXML
	private Label pluginTitleLabel;
	@FXML
	private Label pluginNameLabel;
	@FXML
	private Label pluginVersionLabel;
	@FXML
	private Label pluginBundleIdLabel;
	@FXML
	private Label pluginIdLabel;
	
	@FXML
	private Label pluginPathLabel;
	
	@FXML
	private JFXButton openDirectoryButton;
	
	private Image brickImage  = new Image(getClass().getResourceAsStream("/icons/soundwave-blue-16.png"));;
	private Image directoryImage = new Image(getClass().getResourceAsStream("/icons/folder-grey-16.png"));

	@FXML
	public void initialize() { 
		
		openDirectoryButton.setGraphic(new ImageView(directoryImage));
		openDirectoryButton.setText("");
		openDirectoryButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				File pluginFile = new File(pluginPathLabel.getText());
				PlatformUtils.openDirectoryExplorer(pluginFile.getParentFile());
			};
		});
		
	}
	
	public void setPlugin(Plugin plugin){
		pluginTypeIcon.setImage(brickImage);
		pluginTitleLabel.setText(plugin.getName());
		pluginNameLabel.setText(plugin.getName());
		pluginVersionLabel.setText("");
		pluginIdLabel.setText("");
		pluginBundleIdLabel.setText("");
		pluginPathLabel.setText(plugin.getPath());
	}

}
