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

package com.owlplug.core.controllers.dialogs;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.OptionsController;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.services.NativeHostService;
import com.owlplug.core.services.PluginService;
import com.owlplug.core.ui.RecoveredPluginView;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.host.loaders.NativePluginLoader;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CrashRecoveryDialogController extends AbstractDialogController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  
  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @Autowired
  private PluginService pluginService;
  @Autowired
  private NativeHostService nativeHostService;
  @Autowired 
  private OptionsController optionsController;
  
  
  @FXML
  protected JFXCheckBox nativeDiscoveryCheckbox;
  @FXML
  protected JFXComboBox<NativePluginLoader> pluginNativeComboBox;
  @FXML
  protected Button closeButton;
  @FXML
  protected Hyperlink troubleshootingLink;
  @FXML 
  protected Hyperlink issuesLink;
  @FXML
  protected VBox pluginListContainer;
  @FXML
  protected Pane incompleteSyncPane;
  
  
  CrashRecoveryDialogController() {
    super(600, 550);
    this.setOverlayClose(false);
  }

  /**
   * FXML initialize.
   */
  public void initialize() {
    
    nativeDiscoveryCheckbox.setDisable(!nativeHostService.isNativeHostAvailable());
    nativeDiscoveryCheckbox.setSelected(this.getPreferences().getBoolean(
        ApplicationDefaults.NATIVE_HOST_ENABLED_KEY, false));
    
    nativeDiscoveryCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      this.getPreferences().putBoolean(ApplicationDefaults.NATIVE_HOST_ENABLED_KEY, newValue);
      this.pluginNativeComboBox.setDisable(!newValue);
    });

    ObservableList<NativePluginLoader> pluginLoaders = FXCollections.observableArrayList(
        nativeHostService.getAvailablePluginLoaders());
    pluginNativeComboBox.setItems(pluginLoaders);

    pluginNativeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        this.getPreferences().put(ApplicationDefaults.PREFERRED_NATIVE_LOADER, newValue.getId());
        nativeHostService.setCurrentPluginLoader(newValue);
      }
    });

    pluginNativeComboBox.setDisable(!nativeHostService.isNativeHostAvailable());
    NativePluginLoader pluginLoader = nativeHostService.getCurrentPluginLoader();
    pluginNativeComboBox.getSelectionModel().select(pluginLoader);

    troubleshootingLink.setOnAction((e) -> PlatformUtils.openDefaultBrowser("https://github.com/DropSnorz/OwlPlug/wiki"));
    issuesLink.setOnAction((e) -> PlatformUtils.openDefaultBrowser("https://github.com/DropSnorz/OwlPlug/issues"));
    
    closeButton.setOnAction(e -> {
      optionsController.refreshView();
      this.close();
    });
    
    List<Plugin> incompleteSyncPlugins = pluginService.getSyncIncompletePlugins();
    
    if (incompleteSyncPlugins.size() > 0) {
      incompleteSyncPane.setVisible(true);
      for (Plugin p : incompleteSyncPlugins) {
        log.info("Crash report opened for plugin {}", p.getName());
        RecoveredPluginView pluginView = new RecoveredPluginView(p, pluginService, this.getApplicationDefaults());
        pluginListContainer.getChildren().add(pluginView);
      }
    } else {
      incompleteSyncPane.setVisible(false);
    }
    
  }

  @Override
  protected Node getBody() {
    return lazyViewRegistry.get(LazyViewRegistry.CRASH_RECOVERY_VIEW);
  }

  @Override
  protected Node getHeading() {
    Label title = new Label("Ooh, something wrong happens :(");
    title.getStyleClass().add("heading-3");
    return title;
  }

}
