package com.dropsnorz.owlplug.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.services.PluginExplorer;
import com.jfoenix.controls.JFXTreeView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

@Controller
public class MainController {
	
	@Autowired
	PluginExplorer pluginExplorer;
	
	@FXML
	JFXTreeView<Object> treeView;
	
	@FXML
	BorderPane mainPane;
	
	
    @FXML
    public void initialize() {  
    	
    	
    	TreeItem<Object> treeRootNode = 
    	        new TreeItem<Object>("(all)");
    	
    	List<Plugin> pluginList = pluginExplorer.explore();
    	
    	for(Plugin plugin : pluginList){
    		
    		treeRootNode.getChildren().add(new TreeItem<Object>(plugin.getName()));
    		
    	}
    	
		treeView.setRoot(treeRootNode);

    	
    }

}
