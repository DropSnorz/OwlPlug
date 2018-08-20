package com.dropsnorz.owlplug.core.controllers.dialogs;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.util.prefs.Preferences;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class WelcomeDialogController extends AbstractDialog {
	
	@Autowired
	private LazyViewRegistry lazyViewRegistry;
	@Autowired
	private Preferences prefs;
	@Autowired
	private TaskFactory taskFactory;
	@Autowired
	private ApplicationDefaults applicationDefaults;
	
	@FXML
	private JFXButton openDirectoryButton;
	@FXML
	private JFXTextField directoryTextField;
	@FXML
	private JFXButton okButton;
	@FXML
	private JFXButton cancelButton;
	@FXML
	private Label errorLabel;
	
	
	WelcomeDialogController() {
		super(650,300);
		this.setOverlayClose(false);
	}
	
	/**
	 * FXML initialize.
	 */
	public void initialize() {
		
		errorLabel.setVisible(false);
		
		openDirectoryButton.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			Window mainWindow = openDirectoryButton.getScene().getWindow();

			File selectedDirectory = 
					directoryChooser.showDialog(mainWindow);

			if (selectedDirectory != null) {
				directoryTextField.setText(selectedDirectory.getAbsolutePath());
			}
			errorLabel.setVisible(false);
		});
		
		
		okButton.setOnAction(e -> {
			if (isDirectoryPathValid()) {
				this.close();
				prefs.put(ApplicationDefaults.VST_DIRECTORY_KEY, directoryTextField.getText());
				taskFactory.createPluginSyncTask().run();
			} else {
				errorLabel.setVisible(true);
			}
		});
		
		cancelButton.setOnAction(e -> {
			this.close();
		});
		
	}
	
	private boolean isDirectoryPathValid() {
		File directoryFile = new File(directoryTextField.getText());
		return directoryFile.exists() && directoryFile.isDirectory();
	}

	@Override
	protected Node getBody() {
		return lazyViewRegistry.get(LazyViewRegistry.WELCOME_VIEW);
	}

	@Override
	protected Node getHeading() {
		Label title = new Label("Owlplug is ready to go !");
		title.getStyleClass().add("heading-3");
		ImageView iv = new ImageView(applicationDefaults.rocketImage);
		iv.setFitHeight(20);
		iv.setFitWidth(20);
		title.setGraphic(iv);
		return title;
	}
	
}
