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

package com.owlplug.core.controllers.fragments;

import com.owlplug.core.components.ApplicationPreferences;
import com.owlplug.core.ui.SVGPaths;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.plugin.controllers.dialogs.ListDirectoryDialogController;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.controlsfx.control.ToggleSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginPathFragmentController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @FXML
  private Node mainNode;
  @FXML
  private Label headerLabel;
  @FXML
  private ToggleSwitch activationToggleButton;
  @FXML
  private Hyperlink extraDirectoryLink;
  @FXML
  private TextField directoryTextField;
  @FXML
  private Button directoryButton;
  @FXML
  private Label directoryExistLabel;
  @FXML
  private Label canReadLabel;
  @FXML
  private Label canWriteLabel;

  private SVGPath checkPath = new SVGPath();

  private SVGPath crossPath = new SVGPath();


  private String name;
  private String enableOptionKey;
  private String directoryOptionKey;
  private String extraDirectoryOptionKey;
  private ApplicationPreferences prefs;
  private ListDirectoryDialogController listDirectoryDialogController;

  public PluginPathFragmentController(String name, String enableOptionKey, String directoryOptionKey,
                                      String extraDirectoryOptionKey, ApplicationPreferences prefs,
                                      ListDirectoryDialogController listDirectoryDialogController) {
    this.name = name;
    this.enableOptionKey = enableOptionKey;
    this.directoryOptionKey = directoryOptionKey;
    this.extraDirectoryOptionKey = extraDirectoryOptionKey;
    this.prefs = prefs;
    this.listDirectoryDialogController = listDirectoryDialogController;

    init();

  }

  public void init() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/fragments/PluginPathFragment.fxml"));
      // Set the controller for this specific tab
      loader.setController(this);
      loader.load();

    } catch (IOException e) {
      log.error("Could not init PluginPathFragment", e);
    }

    checkPath.setContent(SVGPaths.check);
    crossPath.setContent(SVGPaths.cross);

    headerLabel.setText(name);
    directoryTextField.setPromptText(name + " plugin directory");

    activationToggleButton.setText("Explore " + name + " plugins");
    activationToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
      prefs.putBoolean(enableOptionKey, newValue);
      refresh();
    });


    directoryButton.setOnAction(e -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      Window mainWindow = directoryButton.getScene().getWindow();
      File selectedDirectory = directoryChooser.showDialog(mainWindow);
      if (selectedDirectory != null) {
        directoryTextField.setText(selectedDirectory.getAbsolutePath());
      }
    });

    directoryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      prefs.put(directoryOptionKey, FileUtils.convertPath(newValue));
      refresh();
    });

    extraDirectoryLink.setOnAction(e -> {
      listDirectoryDialogController.configure(extraDirectoryOptionKey);
      listDirectoryDialogController.show();
    });

    refresh();

  }

  public void disable() {
    activationToggleButton.setSelected(false);
    directoryTextField.setDisable(true);
    directoryButton.setDisable(true);
    extraDirectoryLink.setDisable(true);
    activationToggleButton.setDisable(true);

  }

  private DirectoryChecks checkDirectory(String path) {

    DirectoryChecks checks = new DirectoryChecks();

    // Handle blank or null path
    if (path == null || path.isBlank()) {
      checks.setExists(new DirectoryCheck(false, "Directory path is blank or not set."));
      checks.setCanRead(new DirectoryCheck(false, "Cannot check read permission: path is blank."));
      checks.setCanWrite(new DirectoryCheck(false, "Cannot check write permission: path is blank."));
      return checks;
    }

    File dir = new File(path);
    boolean exists = dir.exists() && dir.isDirectory();

    // Exists check
    DirectoryCheck existsCheck;
    if (exists) {
      existsCheck = new DirectoryCheck(true, "Directory exists.");
    } else {
      existsCheck = new DirectoryCheck(false, "Directory does not exist.");
    }
    checks.setExists(existsCheck);

    // Read check
    DirectoryCheck readCheck;
    if (!exists) {
      readCheck = new DirectoryCheck(false, "Cannot check read permissions: directory does not exist.");
    } else if (dir.canRead()) {
      readCheck = new DirectoryCheck(true, "Directory is readable.");
    } else {
      readCheck = new DirectoryCheck(false, "Directory is not readable.");
    }
    checks.setCanRead(readCheck);

    // Write check
    DirectoryCheck writeCheck;
    if (!exists) {
      writeCheck = new DirectoryCheck(false, "Cannot check write permission: directory does not exist.");
    } else if (dir.canWrite()) {
      writeCheck = new DirectoryCheck(true, "Directory is writable.");
    } else {
      writeCheck = new DirectoryCheck(false, "Directory is not writable.");
    }
    checks.setCanWrite(writeCheck);

    return checks;

  }

  public void refresh() {

    directoryTextField.setText(prefs.get(directoryOptionKey, ""));

    boolean enabled = prefs.getBoolean(enableOptionKey, false);
    directoryTextField.setDisable(!enabled);
    directoryButton.setDisable(!enabled);
    extraDirectoryLink.setDisable(!enabled);

    directoryExistLabel.setVisible(enabled);
    canReadLabel.setVisible(enabled);
    canWriteLabel.setVisible(enabled);

    activationToggleButton.setSelected(enabled);

    MessageFormat extraDirectoryMessageFormat = new MessageFormat("+ {0} additional directories");
    extraDirectoryLink.setText(extraDirectoryMessageFormat.format(
      new Object[] {
        prefs.getList(extraDirectoryOptionKey).size()
      }
    ));

    DirectoryChecks checks = checkDirectory(directoryTextField.getText());

    directoryExistLabel.getStyleClass().removeAll("label-disabled", "label-danger");
    Region imv = (Region) directoryExistLabel.getGraphic();
    if (checks.getExists().status()) {
      directoryExistLabel.getStyleClass().add("label-disabled");
      imv.setShape(checkPath);
      imv.setStyle("-fx-background-color: disabled-color;");
    } else {
      directoryExistLabel.getStyleClass().add("label-danger");
      imv.setShape(crossPath);
      imv.setStyle("-fx-background-color: danger-color;");
    }
    directoryExistLabel.setTooltip(new Tooltip(checks.getExists().message()));

    imv = (Region) canReadLabel.getGraphic();
    canReadLabel.getStyleClass().removeAll("label-disabled", "label-danger");
    if (checks.getCanRead().status()) {
      canReadLabel.getStyleClass().add("label-disabled");
      imv.setShape(checkPath);
      imv.setStyle("-fx-background-color: disabled-color;");
    } else {
      canReadLabel.getStyleClass().add("label-danger");
      imv.setShape(crossPath);
      imv.setStyle("-fx-background-color: danger-color;");
    }
    canReadLabel.setTooltip(new Tooltip(checks.getCanRead().message()));

    imv = (Region) canWriteLabel.getGraphic();
    canWriteLabel.getStyleClass().removeAll("label-disabled", "label-danger");
    if (checks.getCanWrite().status()) {
      canWriteLabel.getStyleClass().add("label-disabled");
      imv.setShape(checkPath);
      imv.setStyle("-fx-background-color: disabled-color;");
    } else {
      canWriteLabel.getStyleClass().add("label-danger");
      imv.setShape(crossPath);
      imv.setStyle("-fx-background-color: danger-color;");
    }
    canWriteLabel.setTooltip(new Tooltip(checks.getCanWrite().message()));
  }

  public Node getNode() {
    return mainNode;
  }

  public static class DirectoryChecks {
    private DirectoryCheck exists;
    private DirectoryCheck canRead;
    private DirectoryCheck canWrite;

    public DirectoryCheck getExists() {
      return exists;
    }

    public DirectoryCheck getCanRead() {
      return canRead;
    }

    public DirectoryCheck getCanWrite() {
      return canWrite;
    }

    public void setExists(DirectoryCheck exists) {
      this.exists = exists;
    }

    public void setCanRead(DirectoryCheck canRead) {
      this.canRead = canRead;
    }

    public void setCanWrite(DirectoryCheck canWrite) {
      this.canWrite = canWrite;
    }
  }

  public record DirectoryCheck(boolean status, String message) {
  }
}
