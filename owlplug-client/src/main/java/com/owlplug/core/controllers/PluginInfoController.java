package com.owlplug.core.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.controllers.dialogs.DialogController;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.services.PluginService;
import com.owlplug.core.utils.PlatformUtils;
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
  private ImageCache imageCache;
  @Autowired
  private ApplicationDefaults applicationDefaults;
  @Autowired
  private CoreTaskFactory coreTaskFactory;

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
  private Label pluginIdentifierLabel;
  @FXML
  private Label pluginManufacturerLabel;
  @FXML
  private Label pluginCategoryLabel;
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

    pluginScreenshotPane.setEffect(new ColorAdjust(0, 0, -0.6, 0));

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
        coreTaskFactory.createPluginRemoveTask(currentPlugin)
            .setOnSucceeded(x -> pluginsController.refreshPlugins()).schedule();
      });
      removeButton.getStyleClass().add("button-danger");

      layout.setActions(removeButton, cancelButton);
      dialog.setContent(layout);
      dialog.show();
    });
  }

  public void setPlugin(Plugin plugin) {
    this.currentPlugin = plugin;
    pluginFormatIcon.setImage(applicationDefaults.getPluginFormatIcon(plugin));
    pluginFormatLabel.setText(plugin.getFormat().getText() + " Plugin");
    pluginTitleLabel.setText(plugin.getName());
    pluginNameLabel.setText(Optional.ofNullable(plugin.getDescriptiveName()).orElse(plugin.getName()));
    pluginVersionLabel.setText(Optional.ofNullable(plugin.getVersion()).orElse("Unknown"));
    pluginManufacturerLabel.setText(Optional.ofNullable(plugin.getManufacturerName()).orElse("Unknown"));
    pluginIdentifierLabel.setText(Optional.ofNullable(plugin.getUid()).orElse("Unknown"));
    pluginCategoryLabel.setText(Optional.ofNullable(plugin.getCategory()).orElse("Unknown"));
    pluginPathLabel.setText(plugin.getPath());

    setPluginImage();
  }

  private void setPluginImage() {
    if (currentPlugin.getScreenshotUrl() == null || currentPlugin.getScreenshotUrl().isEmpty()) {
      String url = pluginService.resolveImageUrl(currentPlugin);
      currentPlugin.setScreenshotUrl(url);
      pluginService.save(currentPlugin);
    }

    String url = currentPlugin.getScreenshotUrl();
    if (knownPluginImages.contains(url) && !imageCache.contains(url)) {

      BackgroundImage bgImg = new BackgroundImage(applicationDefaults.pluginPlaceholderImage,
          BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
          new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
      pluginScreenshotPane.setBackground(new Background(bgImg));

    } else {
      
      this.knownPluginImages.add(url);
      Image screenshot = imageCache.get(url);
      if (screenshot != null) {
        BackgroundImage bgImg = new BackgroundImage(screenshot, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
        pluginScreenshotPane.setBackground(new Background(bgImg));
      }

    }
  }

}
