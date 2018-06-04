package com.dropsnorz.owlplug.core.controllers.dialogs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
import com.dropsnorz.owlplug.core.controllers.IEntityCreateOrUpdate;
import com.dropsnorz.owlplug.core.dao.GoogleDriveRepositoryDAO;
import com.dropsnorz.owlplug.core.model.FileSystemRepository;
import com.dropsnorz.owlplug.core.model.GoogleDriveRepository;
import com.dropsnorz.owlplug.core.services.PluginRepositoryService;
import com.dropsnorz.owlplug.core.utils.FileUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
@Controller
public class GoogleDriveRepositoryController extends AbstractDialog implements IEntityCreateOrUpdate<GoogleDriveRepository> {

	@Autowired
	LazyViewRegistry viewRegistry;
	@Autowired
	PluginRepositoryService pluginRepositoryService;
	
	@Autowired
	GoogleDriveRepositoryDAO googleDriveRepositoryDAO;
	
	@FXML
	JFXButton addButton;
	
	@FXML
	JFXButton closeButton;
	
	@FXML
	private JFXTextField repositoryNameTextField;
	@FXML
	private JFXTextField googleDirectoryURLTextField;
	@FXML
	private Label messageLabel;
	
	private GoogleDriveRepository currentRepository;
	
	
	public void initialize() {
		
		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				close();
			};
		});


		addButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if(currentRepository != null) {
					updateRepository();
				}
				else {
					createRepository();
				}
			};
		});
	}
	
	@Override
	public void startCreateSequence() {
		this.currentRepository = null;
		
		this.repositoryNameTextField.setText("");
		this.repositoryNameTextField.setDisable(false);
		this.googleDirectoryURLTextField.setText("");
	}

	@Override
	public void startUpdateSequence(GoogleDriveRepository entity) {
		this.currentRepository = entity;
		
		this.repositoryNameTextField.setText(currentRepository.getName());
		this.repositoryNameTextField.setDisable(true);
		this.googleDirectoryURLTextField.setText(currentRepository.getRemoteRessourceId());
				
	}
	
	private void createRepository() {
		
		String name = repositoryNameTextField.getText();
		String id = googleDirectoryURLTextField.getText();

		GoogleDriveRepository repository = new GoogleDriveRepository(name, id, null);

		
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
		
	}
	
	private void updateRepository() {
		
		currentRepository.setRemoteRessourceId(googleDirectoryURLTextField.getText());
		pluginRepositoryService.save(currentRepository);
		
	}

	@Override
	protected Node getNode() {
		return viewRegistry.getAsNode(LazyViewRegistry.NEW_FILESYSTEM_REPOSITORY_VIEW);

	}

}
