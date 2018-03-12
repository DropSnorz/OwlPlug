package com.dropsnorz.owlplug.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.services.PluginExplorer;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTabPane;
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
	
	
	@FXML 
	StackPane rootPane;
	@FXML
	BorderPane mainPane;
	@FXML
	JFXTabPane tabPaneHeader;
	@FXML
	JFXTabPane tabPaneContent;
	@FXML
	JFXDrawer leftDrawer;
		
	
    @FXML
    public void initialize() {  
    	
    	
    	this.tabPaneHeader.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				tabPaneContent.getSelectionModel().select(newValue.intValue());
				
			}
    		
    	});

    }
    
    public StackPane getRootPane() {
    	return rootPane;
    }
    
    public JFXDrawer getLeftDrawer() {
    	return leftDrawer;
    }
    
   
    
   
    

}
