package com.dropsnorz.owlplug.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginDirectory;

import javafx.fxml.FXML;
import javafx.scene.Node;

@Controller
public class NodeInfoController {
	
	@Autowired
	PluginInfoController pluginInfoController;
	
	@Autowired
	DirectoryInfoController directoryInfoController;
	
	@FXML
	Node pluginInfoView;
	@FXML
	Node directoryInfoView;
	
	@FXML
	public void initialize() { 
		
		pluginInfoView.setVisible(false);
		directoryInfoView.setVisible(false);
	}
	
	public void setNode(Object node){
		
		pluginInfoView.setVisible(false);
		directoryInfoView.setVisible(false);
		
		if(node instanceof Plugin){
			pluginInfoController.setPlugin((Plugin)node);
			pluginInfoView.setVisible(true);
		}
		if(node instanceof PluginDirectory){
			directoryInfoController.setPluginDirectory((PluginDirectory)node);
			directoryInfoView.setVisible(true);
		}
	}

}
