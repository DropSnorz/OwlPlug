package com.dropsnorz.owlplug.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.core.controllers.dialogs.FileSystemRepositoryController;
import com.dropsnorz.owlplug.core.controllers.dialogs.GoogleDriveRepositoryController;
import com.dropsnorz.owlplug.core.model.FileSystemRepository;
import com.dropsnorz.owlplug.core.model.GoogleDriveRepository;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.model.PluginRepository;
import com.dropsnorz.owlplug.core.services.PluginRepositoryService;
import com.dropsnorz.owlplug.core.ui.PluginListCellFactory;
import com.dropsnorz.owlplug.core.utils.PlatformUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Controller
public class RepositoryInfoController {

	@Autowired
	private ApplicationDefaults applicationDefaults;
	@Autowired
	private PluginRepositoryService repositoryService;
	@Autowired
	private FileSystemRepositoryController fileSystemRepositoryController;
	@Autowired
	private GoogleDriveRepositoryController googleDriveRepositoryController;
	@Autowired
	private DialogController dialogController;

	@FXML
	private Label repositoryNameLabel;
	@FXML
	private JFXButton openRepositoryButton;
	@FXML
	private JFXButton pullButton;
	@FXML
	private JFXButton deleteButton;
	@FXML
	private JFXButton editButton;
	@FXML
	private JFXListView<Plugin> pluginRepositoryListView;

	private PluginRepository repository;

	public void initialize() {

		pluginRepositoryListView.setCellFactory(new PluginListCellFactory(applicationDefaults));

		pullButton.setOnAction(e -> {
			repositoryService.pull(repository);
		});

		openRepositoryButton.setOnAction(e -> {
			PlatformUtils.openDirectoryExplorer(repositoryService.getLocalRepositoryPath(repository));
		});

		editButton.setOnAction(e ->  {
			if(repository instanceof FileSystemRepository) {
				fileSystemRepositoryController.show();
				fileSystemRepositoryController.startUpdateSequence((FileSystemRepository) repository);
			}
			if(repository instanceof GoogleDriveRepository) {
				googleDriveRepositoryController.show();
				googleDriveRepositoryController.startUpdateSequence((GoogleDriveRepository) repository);
			}
		});

		deleteButton.setOnAction(e -> {

			JFXDialog dialog = dialogController.newDialog();
			JFXDialogLayout layout = new JFXDialogLayout();

			layout.setHeading(new Label("Remove directory"));
			layout.setBody(new Label("Do you really want to remove repository " + repository.getName()
			+ " and all of its content ? This will permanently delete the files from your hard drive."));

			JFXButton cancelButton = new JFXButton("Cancel");
			cancelButton.setOnAction(cancelEvent -> {
				dialog.close();
			});	

			JFXButton removeButton = new JFXButton("Remove");
			removeButton.setOnAction(removeEvent -> {
				dialog.close();
				repositoryService.delete(repository);
			});	
			removeButton.getStyleClass().add("button-danger");

			layout.setActions(removeButton, cancelButton);
			dialog.setContent(layout);
			dialog.show();
		});
	}

	public void setPluginRepository(PluginRepository repository) {

		repositoryNameLabel.setText(repository.getName());
		pluginRepositoryListView.getItems().setAll(repository.getPluginList());
		this.repository = repository;

	}

}
