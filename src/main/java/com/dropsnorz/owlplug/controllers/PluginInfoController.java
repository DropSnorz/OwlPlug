package com.dropsnorz.owlplug.controllers;

import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.Plugin;

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
	private Label pluginPathLabel;
	
	private Image brickImage  = new Image(getClass().getResourceAsStream("/icons/soundwave-blue-16.png"));;

	@FXML
	public void initialize() { 
		
	}
	
	public void setPlugin(Plugin plugin){
		pluginTypeIcon.setImage(brickImage);
		pluginTitleLabel.setText(plugin.getName());
		pluginNameLabel.setText(plugin.getName());
		pluginPathLabel.setText(plugin.getPath());
	}

}
