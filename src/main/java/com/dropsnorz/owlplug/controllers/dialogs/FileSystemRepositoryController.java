package com.dropsnorz.owlplug.controllers.dialogs;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.FileSystemRepository;
import com.dropsnorz.owlplug.services.PluginRepositoryService;
import com.dropsnorz.owlplug.utils.FileUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

@Controller
public class FileSystemRepositoryController extends DialogFrame {

	@Autowired
	PluginRepositoryService pluginRepositoryService;
	
	@FXML
	JFXButton closeButton;

	@FXML
	JFXButton addButton;

	@FXML
	JFXTextField repositoryNameTextField;

	@FXML
	JFXTextField repositoryPathTextField;

	@FXML
	JFXButton browseDirectoryButton;
	
	@FXML
	Label messageLabel;


	public void initialize() {

		setOverlayClose(false);

		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				close();
			};
		});


		addButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {

				String name = repositoryNameTextField.getText();
				String path = repositoryPathTextField.getText();

				FileSystemRepository repository = new FileSystemRepository(name, path);

				
				if(FileUtils.isFilenameValid(name)) {
					if (pluginRepositoryService.createRepository(repository)) {
						close();
					}
					else {
						messageLabel.setText("A repository named " + name + " already exists");
					}

				}
				else {
					messageLabel.setText("Repository name contains illegal characters");
				}

			};
		});

		browseDirectoryButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				Window mainWindow = repositoryPathTextField.getScene().getWindow();

				File selectedDirectory = 
						directoryChooser.showDialog(mainWindow);

				if(selectedDirectory == null){
					repositoryPathTextField.setText("");
				}else{
					repositoryPathTextField.setText(selectedDirectory.getAbsolutePath());
				}
			}
		});

	}



}
