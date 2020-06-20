/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.OptionsController;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.services.NativeHostService;
import com.owlplug.core.services.PluginService;
import com.owlplug.core.ui.RecoveredPluginView;
import com.owlplug.core.utils.PlatformUtils;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CrashRecoveryDialogController extends AbstractDialogController {
  
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
  protected Button closeButton;
  @FXML
  protected Hyperlink troubleshootingLink;
  @FXML 
  protected Hyperlink issuesLink;
  @FXML
  protected VBox pluginListContainer;
  @FXML
  protected Pane uncompleteSyncPane;
  
  
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
    });
    
    troubleshootingLink.setOnAction((e) -> PlatformUtils.openDefaultBrowser("https://github.com/DropSnorz/OwlPlug/wiki"));
    issuesLink.setOnAction((e) -> PlatformUtils.openDefaultBrowser("https://github.com/DropSnorz/OwlPlug/issues"));

    
    closeButton.setOnAction(e -> {
      optionsController.refreshView();
      this.close();
    });
    
    List<Plugin> uncompleteSyncPlugins = pluginService.getsyncUncompletePlugins();
    
    if (uncompleteSyncPlugins.size() > 0) {
      uncompleteSyncPane.setVisible(true);
      for (Plugin p : uncompleteSyncPlugins) {
        RecoveredPluginView pluginView = new RecoveredPluginView(p, pluginService, this.getApplicationDefaults());
        pluginListContainer.getChildren().add(pluginView);
      }
    } else {
      uncompleteSyncPane.setVisible(false);
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
