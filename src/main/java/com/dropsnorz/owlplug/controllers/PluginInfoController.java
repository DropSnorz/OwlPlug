package com.dropsnorz.owlplug.controllers;

import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.Plugin;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Controller
public class PluginInfoController {
	
	@FXML
	Label pluginTitleLabel;
	@FXML
	Label pluginNameLabel;
	@FXML
	Label pluginPathLabel;
	
	@FXML
	public void initialize() { 
		
	}
	
	public void setPlugin(Plugin plugin){
		pluginTitleLabel.setText(plugin.getName());
		pluginNameLabel.setText(plugin.getName());
		pluginPathLabel.setText(plugin.getPath());
	}

}
