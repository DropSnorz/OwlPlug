/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.owlplug.core.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXToggleButton;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.services.PluginService;
import com.owlplug.core.ui.PluginStateView;
import com.owlplug.core.utils.PlatformUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PluginInfoController extends BaseController {

  @Autowired
  private PluginsController pluginsController;
  @Autowired
  private PluginService pluginService;
  @Autowired
  private ImageCache imageCache;
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
  private PluginStateView pluginStateView;
  @FXML
  private Label pluginPathLabel;
  @FXML
  private JFXButton openDirectoryButton;
  @FXML
  private JFXButton enableButton;
  @FXML
  private JFXButton disableButton;
  @FXML
  private JFXButton uninstallButton;
  @FXML
  private JFXToggleButton nativeDiscoveryToggleButton;

  private Plugin currentPlugin = null;
  private ArrayList<String> knownPluginImages = new ArrayList<>();

  /**
   * FXML initialize method.
   */
  @FXML
  public void initialize() {

    pluginScreenshotPane.setEffect(new ColorAdjust(0, 0, -0.6, 0));

    openDirectoryButton.setGraphic(new ImageView(this.getApplicationDefaults().directoryImage));
    openDirectoryButton.setText("");
    openDirectoryButton.setOnAction(e -> {
      File pluginFile = new File(pluginPathLabel.getText());
      PlatformUtils.openDirectoryExplorer(pluginFile.getParentFile());
    });

    uninstallButton.setOnAction(e -> {
      this.showUninstallDialog();

    });

    disableButton.setOnAction(e -> {
      if (this.getPreferences().getBoolean(ApplicationDefaults.SHOW_DIALOG_DISABLE_PLUGIN_KEY, true)) {
        this.showDisableDialog();
      } else {
        pluginService.disablePlugin(currentPlugin);
        setPlugin(currentPlugin);
        pluginsController.refreshPluginTree();
      }

    });

    enableButton.setOnAction(e -> {
      pluginService.enablePlugin(currentPlugin);
      setPlugin(currentPlugin);
      pluginsController.refreshPluginTree();
    });

    nativeDiscoveryToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (currentPlugin != null && currentPlugin.getFootprint() != null) {
        currentPlugin.getFootprint().setNativeDiscoveryEnabled(newValue);
        pluginService.save(currentPlugin.getFootprint());
      }

    });

  }

  public void setPlugin(Plugin plugin) {
    this.currentPlugin = plugin;
    pluginFormatIcon.setImage(this.getApplicationDefaults().getPluginFormatIcon(plugin));
    pluginFormatLabel.setText(plugin.getFormat().getText() + " Plugin");
    pluginTitleLabel.setText(plugin.getName());
    pluginNameLabel.setText(Optional.ofNullable(plugin.getDescriptiveName()).orElse(plugin.getName()));
    pluginVersionLabel.setText(Optional.ofNullable(plugin.getVersion()).orElse("Unknown"));
    pluginManufacturerLabel.setText(Optional.ofNullable(plugin.getManufacturerName()).orElse("Unknown"));
    pluginIdentifierLabel.setText(Optional.ofNullable(plugin.getUid()).orElse("Unknown"));
    pluginCategoryLabel.setText(Optional.ofNullable(plugin.getCategory()).orElse("Unknown"));
    pluginStateView.setPluginState(pluginService.getPluginState(plugin));
    pluginPathLabel.setText(plugin.getPath());

    if (plugin.isDisabled()) {
      enableButton.setManaged(true);
      enableButton.setVisible(true);
      disableButton.setManaged(false);
      disableButton.setVisible(false);
    } else {
      enableButton.setManaged(false);
      enableButton.setVisible(false);
      disableButton.setManaged(true);
      disableButton.setVisible(true);

    }

    if (plugin.getFootprint() != null) {
      nativeDiscoveryToggleButton.setSelected(plugin.getFootprint().isNativeDiscoveryEnabled());
    }

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

      BackgroundImage bgImg = new BackgroundImage(this.getApplicationDefaults().pluginPlaceholderImage,
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
  
  private void showUninstallDialog() {
    
    JFXDialog dialog = this.getDialogManager().newDialog();

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
          .setOnSucceeded(x -> pluginsController.clearAndFillPluginTree()).schedule();
    });
    removeButton.getStyleClass().add("button-danger");

    layout.setActions(removeButton, cancelButton);
    dialog.setContent(layout);
    dialog.show();
  }
  
  private void showDisableDialog() {
    
    JFXDialogLayout layout = new JFXDialogLayout();

    layout.setHeading(new Label("Disable plugin"));
    
    VBox vbox = new VBox(10);
    Label dialogLabel = new Label(
        "Disabling a plugin will rename the plugin file by updating the extension. "
        + "The suffix \".disabled\" will be appended to the filename causing the DAW to ignore the plugin. "
        + "You can reactivate the plugin at any time from OwlPlug or by renaming the file manually.");
    dialogLabel.setWrapText(true);
    vbox.getChildren().add(dialogLabel);

    Label noteLabel = new Label("You may need admin privileges to rename plugins");
    noteLabel.getStyleClass().add("label-disabled");
    vbox.getChildren().add(noteLabel);
    
    JFXCheckBox displayDialog = new JFXCheckBox("Don't show me this message again");
    VBox.setMargin(displayDialog, new Insets(20,0,0,0));
    displayDialog.setSelected(!getPreferences().getBoolean(ApplicationDefaults.SHOW_DIALOG_DISABLE_PLUGIN_KEY, true));
    displayDialog.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().putBoolean(ApplicationDefaults.SHOW_DIALOG_DISABLE_PLUGIN_KEY, !newValue);
    });
    vbox.getChildren().add(displayDialog);
    layout.setBody(vbox);
    
    JFXDialog dialog = this.getDialogManager().newDialog();

    JFXButton cancelButton = new JFXButton("Cancel");
    cancelButton.setOnAction(cancelEvent -> {
      dialog.close();
    });

    JFXButton disableButton = new JFXButton("Disable Plugin");
    disableButton.setOnAction(removeEvent -> {
      pluginService.disablePlugin(currentPlugin);
      setPlugin(currentPlugin);
      pluginsController.refreshPluginTree();
      dialog.close();
    });

    layout.setActions(disableButton, cancelButton);
    layout.setPrefSize(600, 280);
    dialog.setContent(layout);
    dialog.show();
  }

}
