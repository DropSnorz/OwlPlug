package com.dropsnorz.owlplug.controllers;

import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginDirectory;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

@Controller
public class DirectoryInfoController {

	@FXML
	Label directoryPathLabel;
	
	@FXML
	ListView<Plugin> pluginDirectoryListView;
	
	@FXML
	public void initialize() { 
		

	}
	
	
	public void setPluginDirectory(PluginDirectory pluginDirectory){
		
		directoryPathLabel.setText(pluginDirectory.getPath());
		
		pluginDirectoryListView.getItems().setAll(pluginDirectory.getPluginList());
	}

}

