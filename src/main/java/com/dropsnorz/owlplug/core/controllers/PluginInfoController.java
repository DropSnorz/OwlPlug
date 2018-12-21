package com.dropsnorz.owlplug.core.controllers;

import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.ImageCache;
import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.core.dao.PluginDAO;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.services.PluginService;
import com.dropsnorz.owlplug.core.tasks.PluginRemoveTask;
import com.dropsnorz.owlplug.core.utils.PlatformUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PluginInfoController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DialogController dialogController;
	@Autowired
	private PluginsController pluginsController;
	@Autowired
	private PluginService pluginService;
	@Autowired
	private PluginDAO pluginDAO;
	@Autowired
	private ImageCache imageCache;
	@Autowired
	private ApplicationDefaults applicationDefaults;
	@Autowired
	private TaskFactory taskFactory;

	@FXML 
	private Pane pluginScreenshotPane;
	@FXML
	private ImageView pluginFormatIcon;
	@FXML
	private Label pluginFormatLabel;
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

	/**
	 * FXML initialize method.
	 */
	@FXML
	public void initialize() { 
		
		pluginScreenshotPane.setEffect(new ColorAdjust(0,0,-0.6,0));	

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
			cancelButton.setOnAction(cancelEvent -> {
				dialog.close();
			});	

			JFXButton removeButton = new JFXButton("Remove");
			removeButton.setOnAction(removeEvent -> {
				dialog.close();
				taskFactory.create(new PluginRemoveTask(currentPlugin, pluginDAO))
					.setOnSucceeded(x -> pluginsController.refreshPlugins())
					.schedule();
			});	
			removeButton.getStyleClass().add("button-danger");

			layout.setActions(removeButton, cancelButton);
			dialog.setContent(layout);
			dialog.show();
		});		
	}

	public void setPlugin(Plugin plugin){
		this.currentPlugin = plugin;
		pluginFormatIcon.setImage(applicationDefaults.getPluginFormatIcon(plugin));
		pluginFormatLabel.setText(plugin.getFormat().getText() + " Plugin");
		pluginTitleLabel.setText(plugin.getName());
		pluginNameLabel.setText(plugin.getName());
		pluginVersionLabel.setText(Optional.ofNullable(plugin.getVersion()).orElse(""));
		pluginIdLabel.setText("");
		pluginBundleIdLabel.setText(Optional.ofNullable(plugin.getBundleId()).orElse(""));
		pluginPathLabel.setText(plugin.getPath());
		
		setPluginImage();
	}
	
	
	private void setPluginImage() {
		if (currentPlugin.getScreenshotUrl() == null || currentPlugin.getScreenshotUrl().isEmpty()) {
			String url = pluginService.resolveImageUrl(currentPlugin);
			currentPlugin.setScreenshotUrl(url);
			pluginDAO.save(currentPlugin);
		}
		
		String url = currentPlugin.getScreenshotUrl();
		if (knownPluginImages.contains(url) && !imageCache.contains(url)) {
			
			BackgroundImage bgImg = new BackgroundImage(applicationDefaults.pluginPlaceholderImage, 
					BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
					BackgroundPosition.CENTER, 
					new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
			pluginScreenshotPane.setBackground(new Background(bgImg));
			
		} else {
			this.knownPluginImages.add(url);
			
			Image screenshot = imageCache.get(url);
			
			if (screenshot != null) {
				BackgroundImage bgImg = new BackgroundImage(screenshot, 
						BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
						BackgroundPosition.CENTER, 
						new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
				pluginScreenshotPane.setBackground(new Background(bgImg));
			}
			
		}
	}

}
