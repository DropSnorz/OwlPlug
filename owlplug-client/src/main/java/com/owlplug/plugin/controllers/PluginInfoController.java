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

package com.owlplug.plugin.controllers;

import com.owlplug.controls.Dialog;
import com.owlplug.controls.DialogLayout;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.plugin.components.PluginTaskFactory;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.plugin.controllers.dialogs.DisablePluginDialogController;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginComponent;
import com.owlplug.plugin.services.PluginService;
import com.owlplug.plugin.ui.PluginComponentCellFactory;
import com.owlplug.plugin.ui.PluginStateView;
import com.owlplug.core.utils.PlatformUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import org.controlsfx.control.ToggleSwitch;
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
  private PluginTaskFactory pluginTaskFactory;
  @Autowired
  private DisablePluginDialogController disableController;

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
  private Button openDirectoryButton;
  @FXML
  private Button enableButton;
  @FXML
  private Button disableButton;
  @FXML
  private Button uninstallButton;
  @FXML
  private ListView<PluginComponent> pluginComponentListView;
  @FXML
  private ToggleSwitch nativeDiscoveryToggleButton;

  private Plugin plugin = null;
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
      PlatformUtils.openFromDesktop(pluginFile.getParentFile());
    });

    uninstallButton.setOnAction(e -> {
      this.showUninstallDialog();

    });

    disableButton.setOnAction(e -> {
      if (this.getPreferences().getBoolean(ApplicationDefaults.SHOW_DIALOG_DISABLE_PLUGIN_KEY, true)) {
        this.disableController.setPlugin(plugin);
        this.disableController.show();
      } else {
        this.disableController.disablePluginWithoutPrompt(plugin);
      }
    });

    enableButton.setOnAction(e -> {
      pluginService.enablePlugin(plugin);
      setPlugin(plugin);
      pluginsController.refresh();
    });

    pluginComponentListView.setCellFactory(new PluginComponentCellFactory(this.getApplicationDefaults()));

    nativeDiscoveryToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (plugin != null && plugin.getFootprint() != null) {
        plugin.getFootprint().setNativeDiscoveryEnabled(newValue);
        pluginService.save(plugin.getFootprint());
      }
    });

  }

  public void setPlugin(Plugin plugin) {
    this.plugin = plugin;
    refresh();
  }

  public void refresh() {

    if (plugin == null) {
      return;
    }
    pluginFormatIcon.setImage(this.getApplicationDefaults().getPluginFormatIcon(plugin.getFormat()));
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

    ObservableList<PluginComponent> components = FXCollections.observableList(new ArrayList(plugin.getComponents()));
    pluginComponentListView.setItems(components);

    setPluginImage();
  }

  private void setPluginImage() {
    String url = plugin.getScreenshotUrl();
    if (url == null || url.isEmpty()) {
      // Fallback to footprint screenshot URL
      String footprintUrl = plugin.getFootprint().getScreenshotUrl();
      if (footprintUrl == null || footprintUrl.isEmpty()) {
        // Resolve and save if footprint URL is also missing
        pluginService.tryResolveAndSaveImageUrl(plugin);
      }
      url = plugin.getFootprint().getScreenshotUrl();
    }

    if (url == null || (knownPluginImages.contains(url) && !imageCache.contains(url))) {

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
    
    Dialog dialog = this.getDialogManager().newDialog();
    DialogLayout layout = new DialogLayout();

    layout.setHeading(new Label("Remove plugin"));
    layout.setBody(new Label("Do you really want to remove " + plugin.getName()
        + " ? This will permanently delete the file from your hard drive."));

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(cancelEvent -> {
      dialog.close();
    });

    Button removeButton = new Button("Remove");
    removeButton.setOnAction(removeEvent -> {
      dialog.close();
      pluginTaskFactory.createPluginRemoveTask(plugin)
          .setOnSucceeded(x -> pluginsController.displayPlugins()).schedule();
    });
    removeButton.getStyleClass().add("button-danger");

    layout.setActions(removeButton, cancelButton);
    dialog.setContent(layout);
    dialog.show();
  }

}
