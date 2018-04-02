package com.dropsnorz.owlplug.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.controllers.dialogs.DialogController;

@Controller
public class RepositoryController {
	
	@Autowired
	DialogController dialogController;
	
	RepositoryController(){
		
	}
	
	public void setupFileSystemRepository() {
		
	}

}
