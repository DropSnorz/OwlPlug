package com.dropsnorz.owlplug.core.controllers;

import java.io.File;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.services.PluginService;
import com.dropsnorz.owlplug.core.services.TaskFactory;
import com.dropsnorz.owlplug.core.utils.PlatformUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

@Controller
public class PluginInfoController {

	@Autowired
	private DialogController dialogController;
	
	@Autowired
	private PluginService pluginService;
	
	@Autowired
	private ApplicationDefaults applicationDefaults;
	
	@FXML
	private ImageView pluginTypeIcon;
	@FXML
	private Label pluginTitleLabel;
	@FXML
	private Label pluginNameLabel;
	@FXML
	private Label pluginVersionLabel;
	@FXML
	private Label pluginBundleIdLabel;
	@FXML
	private Label pluginIdLabel;

	@FXML
	private Label pluginPathLabel;

	@FXML
	private JFXButton openDirectoryButton;

	@FXML
	private JFXButton uninstallButton;

	private Plugin currentPlugin = null;
	
	@FXML
	public void initialize() { 

		openDirectoryButton.setGraphic(new ImageView(applicationDefaults.directoryImage));
		openDirectoryButton.setText("");
		openDirectoryButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				File pluginFile = new File(pluginPathLabel.getText());
				PlatformUtils.openDirectoryExplorer(pluginFile.getParentFile());
			};
		});

		uninstallButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				
				
				JFXDialog dialog = dialogController.newDialog();
				
				JFXDialogLayout layout = new JFXDialogLayout();
				
				layout.setHeading(new Label("Remove plugin"));
				layout.setBody(new Label("Do you really want to remove " + currentPlugin.getName() 
				+ " ? This will permanently delete the file from your hard drive."));
				
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
						pluginService.removePlugin(currentPlugin);
					};
				});	
				removeButton.getStyleClass().add("button-danger");
				
				layout.setActions(removeButton, cancelButton);
				dialog.setContent(layout);
				dialog.show();
				
			};
		});		
	}

	public void setPlugin(Plugin plugin){
		this.currentPlugin = plugin;
		pluginTypeIcon.setImage(applicationDefaults.getPluginIcon(plugin));
		pluginTitleLabel.setText(plugin.getName());
		pluginNameLabel.setText(plugin.getName());
		pluginVersionLabel.setText(Optional.ofNullable(plugin.getVersion()).orElse(""));
		pluginIdLabel.setText("");
		pluginBundleIdLabel.setText(Optional.ofNullable(plugin.getBundleId()).orElse(""));
		pluginPathLabel.setText(plugin.getPath());
	}

}
