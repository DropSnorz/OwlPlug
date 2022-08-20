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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.owlplug.core.components.ApplicationPreferences;
import com.owlplug.core.controllers.dialogs.ListDirectoryDialogController;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginPathFragmentController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @FXML
  private Node mainNode;
  @FXML
  private Label headerLabel;
  @FXML
  private JFXToggleButton activationToggleButton;
  @FXML
  private Hyperlink extraDirectoryLink;
  @FXML
  private JFXTextField directoryTextField;
  @FXML
  private JFXButton directoryButton;

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

    headerLabel.setText(name);
    directoryTextField.setPromptText(name + " plugin directory");

    activationToggleButton.setText("Explore " + name + " plugins");
    activationToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
      prefs.putBoolean(enableOptionKey, newValue);
      directoryTextField.setDisable(!newValue);
      directoryButton.setDisable(!newValue);
      extraDirectoryLink.setDisable(!newValue);
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
      prefs.put(directoryOptionKey, newValue);
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

  public void refresh() {

    directoryTextField.setText(prefs.get(directoryOptionKey, ""));

    boolean enabled = prefs.getBoolean(enableOptionKey, false);
    directoryTextField.setDisable(!enabled);
    directoryButton.setDisable(!enabled);
    extraDirectoryLink.setDisable(!enabled);

    activationToggleButton.setSelected(enabled);

    MessageFormat extraDirectoryMessageFormat = new MessageFormat("+ {0} additional directories");
    extraDirectoryLink.setText(extraDirectoryMessageFormat.format(
      new Object[] {
        prefs.getList(extraDirectoryOptionKey).size()
      }
    ));

  }

  public Node getNode() {
    return mainNode;
  }

}
