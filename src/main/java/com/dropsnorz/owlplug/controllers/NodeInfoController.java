package com.dropsnorz.owlplug.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.Plugin;

import javafx.fxml.FXML;
import javafx.scene.Node;

@Controller
public class NodeInfoController {
	
	@Autowired
	PluginInfoController pluginInfoController;
	
	@FXML
	Node pluginInfoView;
	
	@FXML
	public void initialize() { 
		
		pluginInfoView.setVisible(false);
	}
	
	public void setNode(Object node){
		
		pluginInfoView.setVisible(false);
		
		if(node instanceof Plugin){
			pluginInfoController.setPlugin((Plugin)node);
			pluginInfoView.setVisible(true);
		}
	}

}
