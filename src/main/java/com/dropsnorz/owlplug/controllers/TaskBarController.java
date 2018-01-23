package com.dropsnorz.owlplug.controllers;

import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

@Controller
public class TaskBarController {
	
	@FXML
	protected Label taskLabel;
	@FXML
	protected ProgressBar taskProgressBar;
	
	
	@FXML
	public void initialize() {
		
		
	}
	
	
}
