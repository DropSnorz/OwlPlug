package com.dropsnorz.owlplug.core.controllers;

import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.model.PluginDirectory;
import com.dropsnorz.owlplug.core.tasks.DirectoryRemoveTask;
import com.dropsnorz.owlplug.core.ui.PluginListCellFactory;
import com.dropsnorz.owlplug.core.utils.PlatformUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public class DirectoryInfoController {

	@Autowired
	private ApplicationDefaults applicationDefaults;
	@Autowired
	private DialogController dialogController;
	@Autowired
	private TaskFactory taskFactory;

	@FXML
	private Label directoryPathLabel;
	@FXML
	private JFXListView<Plugin> pluginDirectoryListView;
	@FXML 
	private JFXButton openDirectoryButton;
	@FXML 
	private JFXButton deleteDirectoryButton;

	private PluginDirectory pluginDirectory;


	@FXML
	public void initialize() { 

		openDirectoryButton.setGraphic(new ImageView(applicationDefaults.directoryImage));
		openDirectoryButton.setOnAction(e -> {
			PlatformUtils.openDirectoryExplorer(pluginDirectory.getPath());
		});

		pluginDirectoryListView.setCellFactory(new PluginListCellFactory(applicationDefaults));

		deleteDirectoryButton.setOnAction(e ->  {
			JFXDialog dialog = dialogController.newDialog();
			JFXDialogLayout layout = new JFXDialogLayout();

			layout.setHeading(new Label("Remove directory"));
			layout.setBody(new Label("Do you really want to remove " + pluginDirectory.getName()
					+ " and all of its content ? This will permanently delete the file from your hard drive."));

			JFXButton cancelButton = new JFXButton("Cancel");

			cancelButton.setOnAction(cancelEvent -> {
				dialog.close();
			});	

			JFXButton removeButton = new JFXButton("Remove");
			removeButton.setOnAction(removeEvent -> {
				dialog.close(); 
				taskFactory.create(new DirectoryRemoveTask(pluginDirectory))
						.setOnSucceeded(x -> taskFactory.createPluginSyncTask().schedule())
						.schedule();
			});	
			removeButton.getStyleClass().add("button-danger");

			layout.setActions(removeButton, cancelButton);
			dialog.setContent(layout);
			dialog.show();
		});
	}


	public void setPluginDirectory(PluginDirectory pluginDirectory){
		this.pluginDirectory = pluginDirectory;
		directoryPathLabel.setText(pluginDirectory.getPath());
		pluginDirectoryListView.getItems().setAll(pluginDirectory.getPluginList());
	}

}

