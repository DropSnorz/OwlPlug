package com.dropsnorz.owlplug.core.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeView;

import javafx.application.Platform;
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
	@Autowired
	private DialogController dialogController;
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
	JFXComboBox accountComboBox;

	@FXML
	public void initialize() {  


		this.tabPaneHeader.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				tabPaneContent.getSelectionModel().select(newValue.intValue());
				leftDrawer.close();

			}

		});

		accountComboBox.getItems().add("Create new account");

		accountComboBox.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			
			
			if(newValue != null) {
				Parent node = loadFxml("/fxml/dialogs/NewAccount.fxml");
				JFXDialog dialog = dialogController.newBigDialog(node);
				dialog.show();
				
				// Delay comboBox selector change
				Platform.runLater(() -> accountComboBox.setValue(oldValue));
				
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
