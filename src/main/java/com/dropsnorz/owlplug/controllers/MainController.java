package com.dropsnorz.owlplug.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.Plugin;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

@Controller
public class MainController {
	
	
	@Autowired
	private ApplicationContext context;
	
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
				leftDrawer.close();
				
			}
    		
    	});

    }
    
    public StackPane getRootPane() {
    	return rootPane;
    }
    
    public JFXDrawer getLeftDrawer() {
    	return leftDrawer;
    }
    
    public void setLeftDrawerFXML(String ressource) {
       
    	Parent node = loadFxml(ressource);
    	if(node != null) {
    		leftDrawer.setSidePane(node);
    	}
    }
    
    public Parent loadFxml(String ressource) {
    	 FXMLLoader loader = new FXMLLoader(getClass().getResource(ressource));
         loader.setControllerFactory(context::getBean);
         try {
 			Parent node = loader.load();
 			return node;
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
         
         return null;
    }
    

}
