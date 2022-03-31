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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.OptionsController;
import com.owlplug.core.model.platform.OperatingSystem;
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
public class WelcomeDialogController extends AbstractDialogController {

  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @Autowired
  private CoreTaskFactory taskFactory;
  @Autowired
  private OptionsController optionsController;

  @FXML
  private JFXToggleButton vst2ToggleButton;
  @FXML
  private JFXButton vst2DirectoryButton;
  @FXML
  private JFXTextField vst2DirectoryTextField;
  @FXML
  private JFXToggleButton vst3ToggleButton;
  @FXML
  private JFXButton vst3DirectoryButton;
  @FXML
  private JFXTextField vst3DirectoryTextField;
  @FXML
  private JFXToggleButton auToggleButton;
  @FXML
  private JFXButton auDirectoryButton;
  @FXML
  private JFXTextField auDirectoryTextField;
  @FXML
  private JFXButton okButton;
  @FXML
  private JFXButton cancelButton;

  WelcomeDialogController() {
    super(700, 300);
    this.setOverlayClose(false);
  }

  /**
   * FXML initialize.
   */
  public void initialize() {


    vst2DirectoryButton.setOnAction(e -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      Window mainWindow = vst2DirectoryButton.getScene().getWindow();

      File selectedDirectory = directoryChooser.showDialog(mainWindow);

      if (selectedDirectory != null) {
        vst2DirectoryTextField.setText(selectedDirectory.getAbsolutePath());
      }
    });
    
    vst3DirectoryButton.setOnAction(e -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      Window mainWindow = vst3DirectoryButton.getScene().getWindow();

      File selectedDirectory = directoryChooser.showDialog(mainWindow);

      if (selectedDirectory != null) {
        vst3DirectoryTextField.setText(selectedDirectory.getAbsolutePath());
      }
    });
    
    auDirectoryButton.setOnAction(e -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      Window mainWindow = auDirectoryButton.getScene().getWindow();

      File selectedDirectory = directoryChooser.showDialog(mainWindow);

      if (selectedDirectory != null) {
        auDirectoryTextField.setText(selectedDirectory.getAbsolutePath());
      }
    });

    okButton.setOnAction(e -> {
      this.close();
      this.getPreferences().putBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, vst2ToggleButton.isSelected());
      this.getPreferences().put(ApplicationDefaults.VST_DIRECTORY_KEY, vst2DirectoryTextField.getText());
      this.getPreferences().putBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, vst3ToggleButton.isSelected());
      this.getPreferences().put(ApplicationDefaults.VST3_DIRECTORY_KEY, vst3DirectoryTextField.getText());
      optionsController.refreshView();
      taskFactory.createPluginSyncTask().schedule();

    });

    cancelButton.setOnAction(e -> {
      this.close();
    });

    this.refreshView();

  }

  public void refreshView() {

    vst2ToggleButton.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false));
    vst2DirectoryTextField.setText(this.getPreferences().get(ApplicationDefaults.VST_DIRECTORY_KEY, ""));

    vst3ToggleButton.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false));
    vst3DirectoryTextField.setText(this.getPreferences().get(ApplicationDefaults.VST3_DIRECTORY_KEY, ""));

    auToggleButton.setSelected(this.getPreferences().getBoolean(ApplicationDefaults.AU_DISCOVERY_ENABLED_KEY, false));
    auDirectoryTextField.setText(this.getPreferences().get(ApplicationDefaults.AU_DIRECTORY_KEY, ""));

    // Disable AU options for non MAC users
    if (!this.getApplicationDefaults().getRuntimePlatform()
      .getOperatingSystem().equals(OperatingSystem.MAC)) {
      //auToggleButton.setSelected(true);
      auToggleButton.setSelected(false);
      auDirectoryTextField.setDisable(true);
      auDirectoryButton.setDisable(true);
      auToggleButton.setDisable(true);
    }

  }

  @Override
  protected void onDialogShow() {
     this.refreshView();
  }

  @Override
  protected Node getBody() {
    return lazyViewRegistry.get(LazyViewRegistry.WELCOME_VIEW);
  }

  @Override
  protected Node getHeading() {
    Label title = new Label("Owlplug is almost ready !");
    title.getStyleClass().add("heading-3");
    ImageView iv = new ImageView(this.getApplicationDefaults().rocketImage);
    iv.setFitHeight(20);
    iv.setFitWidth(20);
    title.setGraphic(iv);
    return title;
  }

}
