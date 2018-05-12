package com.dropsnorz.owlplug.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;

@Controller
public class RepositoryController {
	
	@Autowired
	DialogController dialogController;
	
	RepositoryController(){
		
	}
	
	public void setupFileSystemRepository() {
		
	}

}
