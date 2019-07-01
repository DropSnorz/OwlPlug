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

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.components.LazyViewRegistry;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class NewLinkController extends AbstractDialogController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @Autowired
  private CoreTaskFactory coreTaskFactory;
  @Autowired
  private Preferences preferences;

  @FXML
  private JFXTextField linkSourceParentTextField; 
  @FXML
  private JFXTextField linkSourceNameTextField;
  @FXML
  private JFXTextField linkTargetTextField;
  @FXML
  private JFXButton sourceDirectoryButton;
  @FXML
  private JFXButton targetDirectoryButton;
  @FXML
  private JFXButton createButton;
  @FXML
  private JFXButton cancelButton;
  @FXML
  private Label errorLabel;
  
  private JFXAutoCompletePopup<String> autoCompletePath;

  NewLinkController() {
    super(700, 500);
  }

  /**
   * FXML initialize.
   */
  public void initialize() {
    
    this.errorLabel.setVisible(false);

    cancelButton.setOnAction(e -> {
      this.close();
    });

    createButton.setOnAction(e -> {
      String sourcePath = Paths.get(linkSourceParentTextField.getText(), linkSourceNameTextField.getText()).toString();
      String targetPath = linkTargetTextField.getText();
      if (checkSymlinkCreation(sourcePath, targetPath)) {
        if (createSymlink(sourcePath, targetPath)) {
          coreTaskFactory.createPluginSyncTask(new File(sourcePath).getParent()).schedule();
          setErrorMessage(null);
          this.close();
        }
      }
    });
    
    sourceDirectoryButton.setOnAction(e -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      Window mainWindow = sourceDirectoryButton.getScene().getWindow();
      File selectedDirectory = directoryChooser.showDialog(mainWindow);
      if (selectedDirectory != null) {
        linkSourceParentTextField.setText(selectedDirectory.getAbsolutePath());
      }
    });
    
    targetDirectoryButton.setOnAction(e -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      Window mainWindow = sourceDirectoryButton.getScene().getWindow();
      File selectedDirectory = directoryChooser.showDialog(mainWindow);
      if (selectedDirectory != null) {
        linkTargetTextField.setText(selectedDirectory.getAbsolutePath());
      }
    });
    
    autoCompletePath = new JFXAutoCompletePopup<>();
    autoCompletePath.setSelectionHandler(e -> {
      linkSourceParentTextField.setText(e.getObject().toString());
    });
    
    linkSourceParentTextField.setOnKeyPressed(e -> {
      autoCompletePath.show(linkSourceParentTextField);
    });

  }
  
  @Override
  public void show() {
    autoCompletePath.getSuggestions().clear();
    if (preferences.getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false)) {
      autoCompletePath.getSuggestions().add(preferences.get(ApplicationDefaults.VST_DIRECTORY_KEY,""));
    }
    if (preferences.getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false)) {
      autoCompletePath.getSuggestions().add(preferences.get(ApplicationDefaults.VST3_DIRECTORY_KEY,""));
    }
    super.show();
  }
  
  private boolean createSymlink(String sourcePath, String targetPath) {
    Path link = Paths.get(sourcePath);
    Path target = Paths.get(targetPath);
    try {
      Files.createSymbolicLink(link, target);
      return true;
    } catch (IOException e) {
      setErrorMessage("Error creating Symlink: " + e.getMessage());
      log.error("Error creating Symlink", e);
      return false;
    }
  }

  private boolean checkSymlinkCreation(String sourcePath, String targetPath) {
    
    if (sourcePath == null || "".equals(sourcePath)) {
      setErrorMessage("Error creating Symlink, invalid source path: " + sourcePath);
      return false;
    }
    if (targetPath == null || "".equals(targetPath)) {
      setErrorMessage("Error creating Symlink, invalid target path: " + targetPath);
      return false;
    }
    File sourceFile = new File(sourcePath);
    File targetFile = new File(targetPath);
    if (sourceFile.exists()) {
      setErrorMessage("Error creating Symlink, file already exists: " + sourcePath);
      return false;
    }
    if (Files.isSymbolicLink(Paths.get(sourcePath))) {
      setErrorMessage("Error creating Symlink, file already exists: " + sourcePath);
      return false;
    }
    if (targetFile.exists() && targetFile.isDirectory()) {
      return true;
    }
    setErrorMessage("Error creating Symlink, invalid target directory: " + targetPath);
    return false;
  }
  
  private void setErrorMessage(String errorMessage) {
    if (errorMessage != null) {
      errorLabel.setVisible(true);
      errorLabel.setText(errorMessage);
    } else {
      errorLabel.setVisible(false);
    }
  }

  
  @Override
  protected Node getBody() {
    return lazyViewRegistry.get(LazyViewRegistry.NEW_LINK_VIEW);
  }

  @Override
  protected Node getHeading() {
    Label title = new Label("Create a new Link");
    title.getStyleClass().add("heading-3");
    return title;
  }

}
