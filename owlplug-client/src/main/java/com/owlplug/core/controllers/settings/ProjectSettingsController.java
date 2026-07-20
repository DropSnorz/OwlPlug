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

package com.owlplug.core.controllers.settings;

import com.owlplug.core.components.ApplicationDefaults.Prefs;
import com.owlplug.core.controllers.BaseController;
import java.io.File;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectSettingsController extends BaseController {

  @FXML
  private ListView<String> projectListView;
  @FXML
  private Button addDirButton;
  @FXML
  private Button removeDirButton;

  private ObservableList<String> projectDirectories;

  @FXML
  public void initialize() {
    Label placeholder = new Label("No project directories configured.");
    placeholder.getStyleClass().add("label-disabled");
    projectListView.setPlaceholder(placeholder);

    FontIcon addIcon = new FontIcon("mdi2p-plus");
    addIcon.setIconSize(14);
    addDirButton.setGraphic(addIcon);

    FontIcon removeIcon = new FontIcon("mdi2m-minus");
    removeIcon.setIconSize(14);
    removeDirButton.setGraphic(removeIcon);

    projectDirectories = FXCollections.observableArrayList(
        getPreferences().getList(Prefs.Projects.DIRECTORY, new ArrayList<>()));
    projectListView.setItems(projectDirectories);

    projectDirectories.addListener((ListChangeListener<String>) change ->
        getPreferences().putList(
            Prefs.Projects.DIRECTORY, new ArrayList<>(projectDirectories)));

    addDirButton.setOnAction(e -> {
      DirectoryChooser chooser = new DirectoryChooser();
      Window window = addDirButton.getScene().getWindow();
      File dir = chooser.showDialog(window);
      if (dir != null) {
        String path = dir.getAbsolutePath();
        if (!projectDirectories.contains(path)) projectDirectories.add(path);
      }
    });

    removeDirButton.setOnAction(e -> {
      String selected = projectListView.getSelectionModel().getSelectedItem();
      if (selected != null) projectDirectories.remove(selected);
    });
  }

  public void refresh() {
    projectDirectories.setAll(
        getPreferences().getList(Prefs.Projects.DIRECTORY, new ArrayList<>()));
  }

}