package com.dropsnorz.owlplug.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.core.controllers.MainController;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

@Service
public class NotificationService {
	
	@Autowired
	private MainController mainController;

	
	public void error(String title, String message) {
		 Alert alert = new Alert(AlertType.ERROR);
         alert.setTitle(title);
         alert.setHeaderText(title);
         alert.setContentText(message);
         alert.showAndWait();
	}
	
	public void info(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
	}
	
	
}
