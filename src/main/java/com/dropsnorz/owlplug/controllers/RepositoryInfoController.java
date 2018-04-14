package com.dropsnorz.owlplug.controllers;

import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.PluginRepository;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Controller
public class RepositoryInfoController {
	
	@FXML
	Label repositoryNameLabel;
	
	public void setPluginRepository(PluginRepository repository) {
		
		repositoryNameLabel.setText(repository.getName());
		
	}

}
