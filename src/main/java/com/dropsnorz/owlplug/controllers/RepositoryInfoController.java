package com.dropsnorz.owlplug.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.PluginRepository;
import com.dropsnorz.owlplug.services.PluginRepositoryService;
import com.dropsnorz.owlplug.utils.PlatformUtils;
import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Controller
public class RepositoryInfoController {
		
	@Autowired
	PluginRepositoryService repositoryService;
	@FXML
	Label repositoryNameLabel;
	@FXML
	JFXButton openRepositoryButton;
	@FXML
	JFXButton pullButton;
	
	
	private PluginRepository repository;
	
	public void initialize() {
		
		pullButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				repositoryService.pull(repository);
			};
		});
		
		openRepositoryButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				PlatformUtils.openDirectoryExplorer(repositoryService.getLocalRepositoryPath(repository));
			};
		});
		
		
	}
	
	public void setPluginRepository(PluginRepository repository) {
		
		repositoryNameLabel.setText(repository.getName());
		this.repository = repository;
		
		
		
	}

}
