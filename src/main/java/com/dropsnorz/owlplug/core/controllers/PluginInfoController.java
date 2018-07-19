package com.dropsnorz.owlplug.core.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.ImageCache;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.services.OwlPlugCentralService;
import com.dropsnorz.owlplug.core.services.PluginService;
import com.dropsnorz.owlplug.core.utils.PlatformUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

@Controller
public class PluginInfoController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DialogController dialogController;
	@Autowired
	private PluginService pluginService;
	@Autowired
	private OwlPlugCentralService owlplugCentralService;
	@Autowired
	private ImageCache imageCache;
	@Autowired
	private ApplicationDefaults applicationDefaults;

	@FXML
	private ImageView pluginScreenshot;
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
	private ArrayList<String> knownPluginImages = new ArrayList<>();

	@FXML
	public void initialize() { 

		openDirectoryButton.setGraphic(new ImageView(applicationDefaults.directoryImage));
		openDirectoryButton.setText("");
		openDirectoryButton.setOnAction(e -> {
			File pluginFile = new File(pluginPathLabel.getText());
			PlatformUtils.openDirectoryExplorer(pluginFile.getParentFile());
		});

		uninstallButton.setOnAction(e -> {

			JFXDialog dialog = dialogController.newDialog();

			JFXDialogLayout layout = new JFXDialogLayout();

			layout.setHeading(new Label("Remove plugin"));
			layout.setBody(new Label("Do you really want to remove " + currentPlugin.getName() 
			+ " ? This will permanently delete the file from your hard drive."));

			JFXButton cancelButton = new JFXButton("Cancel");

			cancelButton.setOnAction( cancelEvent -> {
				dialog.close();
			});	

			JFXButton removeButton = new JFXButton("Remove");

			removeButton.setOnAction( removeEvent -> {
				dialog.close();
				pluginService.removePlugin(currentPlugin);
			});	
			removeButton.getStyleClass().add("button-danger");

			layout.setActions(removeButton, cancelButton);
			dialog.setContent(layout);
			dialog.show();

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
		
		setPluginImage();
	}
	
	
	private void setPluginImage() {
		String url = owlplugCentralService.getPluginImageUrl(this.currentPlugin.getName());
		if(knownPluginImages.contains(url) && !imageCache.contains(url)) {
			pluginScreenshot.setImage(applicationDefaults.pluginPlaceholderImage);
			
		}else {
			this.knownPluginImages.add(url);
			imageCache.loadAsync(url, pluginScreenshot);
		}
	}

}
