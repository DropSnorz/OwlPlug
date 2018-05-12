package com.dropsnorz.owlplug.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.core.model.PluginRepository;
import com.dropsnorz.owlplug.core.services.PluginRepositoryService;
import com.dropsnorz.owlplug.core.services.TaskFactory;
import com.dropsnorz.owlplug.core.services.TaskManager;
import com.dropsnorz.owlplug.core.utils.PlatformUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Controller
public class RepositoryInfoController {

	@Autowired
	PluginRepositoryService repositoryService;
	@Autowired
	DialogController dialogController;
	@FXML
	Label repositoryNameLabel;
	@FXML
	JFXButton openRepositoryButton;
	@FXML
	JFXButton pullButton;
	@FXML
	JFXButton deleteButton;

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
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {

				JFXDialog dialog = dialogController.newDialog();

				JFXDialogLayout layout = new JFXDialogLayout();

				layout.setHeading(new Label("Remove directory"));
				layout.setBody(new Label("Do you really want to remove repository " + repository.getName()
				+ " and all of its content ? This will permanently delete the files from your hard drive."));

				JFXButton cancelButton = new JFXButton("Cancel");

				cancelButton.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {
						dialog.close();
					};
				});	

				JFXButton removeButton = new JFXButton("Remove");

				removeButton.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {
						dialog.close();
						repositoryService.delete(repository);
					};
				});	
				removeButton.getStyleClass().add("button-danger");

				layout.setActions(removeButton, cancelButton);
				dialog.setContent(layout);
				dialog.show();

			};
		});



	}

	public void setPluginRepository(PluginRepository repository) {

		repositoryNameLabel.setText(repository.getName());
		this.repository = repository;



	}

}
