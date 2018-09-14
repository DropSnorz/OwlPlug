package com.dropsnorz.owlplug.core.controllers.dialogs;

import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
import com.dropsnorz.owlplug.core.controllers.IEntityCreateOrUpdate;
import com.dropsnorz.owlplug.core.controllers.PluginsController;
import com.dropsnorz.owlplug.core.model.FileSystemRepository;
import com.dropsnorz.owlplug.core.services.PluginRepositoryService;
import com.dropsnorz.owlplug.core.utils.FileUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class FileSystemRepositoryController extends AbstractDialogController implements IEntityCreateOrUpdate<FileSystemRepository> {

	@Autowired
	private PluginRepositoryService pluginRepositoryService;
	@Autowired
	private PluginsController pluginController;
	@Autowired
	private LazyViewRegistry viewRegistry;
	@Autowired
	private ApplicationDefaults applicationDefaults;

	@FXML
	private JFXButton closeButton;
	@FXML
	private JFXButton addButton;
	@FXML
	private JFXTextField repositoryNameTextField;
	@FXML
	private JFXTextField repositoryPathTextField;
	@FXML
	private JFXButton browseDirectoryButton;
	@FXML
	private Label messageLabel;

	private FileSystemRepository currentFileSystemRepository = null;

	public FileSystemRepositoryController(){
		super(600,300);
	}


	public void initialize() {

		closeButton.setOnAction(e ->  {
			close();
		});


		addButton.setOnAction(e -> {
			if (currentFileSystemRepository != null) {
				updateRepository();
			} else {
				createRepository();
			}
			pluginController.refreshPlugins();
		});

		browseDirectoryButton.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			Window mainWindow = repositoryPathTextField.getScene().getWindow();

			File selectedDirectory = directoryChooser.showDialog(mainWindow);

			if (selectedDirectory != null) {
				repositoryPathTextField.setText(selectedDirectory.getAbsolutePath());
			}
		});

	}


	@Override
	public void startCreateSequence() {

		this.currentFileSystemRepository = null; 

		addButton.setText("Add");
		repositoryNameTextField.setText("");
		repositoryNameTextField.setDisable(false);
		repositoryPathTextField.setText("");
	}


	@Override
	public void startUpdateSequence(FileSystemRepository entity) {

		this.currentFileSystemRepository = entity;

		addButton.setText("Edit");
		repositoryNameTextField.setText(entity.getName());
		repositoryNameTextField.setDisable(true);
		repositoryPathTextField.setText(entity.getRemotePath());
	}


	private void createRepository() {

		String name = repositoryNameTextField.getText();
		String path = repositoryPathTextField.getText();

		FileSystemRepository repository = new FileSystemRepository(name, path);

		if (FileUtils.isFilenameValid(name)) {
			if (pluginRepositoryService.createRepository(repository)) {
				close();
			} else {
				messageLabel.setText("A repository named " + name + " already exists");
			}
		} else {
			messageLabel.setText("Repository name contains illegal characters");
		}
	}

	private void updateRepository() {

		String path = repositoryPathTextField.getText();
		currentFileSystemRepository.setRemotePath(path);
		pluginRepositoryService.save(currentFileSystemRepository);

		close();
	}


	@Override
	protected Node getBody() {
		return viewRegistry.getAsNode(LazyViewRegistry.NEW_FILESYSTEM_REPOSITORY_VIEW);
	}

	@Override
	protected Node getHeading() {
		Label title = new Label("FileSystem Repository");
		title.getStyleClass().add("heading-3");

		ImageView iv = new ImageView(applicationDefaults.fileSystemRepositoryImage);
		iv.setFitHeight(20);
		iv.setFitWidth(20);
		title.setGraphic(iv);
		return title;
	}
}
