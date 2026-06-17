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

import com.owlplug.controls.DialogLayout;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.events.PreferencesChangedEvent;
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
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;

@Controller
public class ListDirectoryDialogController extends AbstractDialogController {

  @Autowired
  private ApplicationEventPublisher publisher;
  @Autowired
  private LazyViewRegistry lazyViewRegistry;

  @FXML
  private ListView<String> directoryListView;
  @FXML
  private Button addDirectoryButton;
  @FXML
  private Button removeDirectoryButton;
  @FXML
  private Button closeButton;

  private String currentPreferenceKey;
  private ObservableList<String> observableItems;

  public ListDirectoryDialogController() {
    super(700,500);
  }

  public void initialize() {
    Label placeholder = new Label("No additional directories configured.");
    placeholder.getStyleClass().add("label-disabled");
    directoryListView.setPlaceholder(placeholder);

    FontIcon addIcon = new FontIcon("mdi2p-plus");
    addIcon.setIconSize(14);
    addDirectoryButton.setGraphic(addIcon);

    FontIcon removeIcon = new FontIcon("mdi2m-minus");
    removeIcon.setIconSize(14);
    removeDirectoryButton.setGraphic(removeIcon);

    addDirectoryButton.setOnAction(e -> {
      DirectoryChooser chooser = new DirectoryChooser();
      File dir = chooser.showDialog(addDirectoryButton.getScene().getWindow());
      if (dir != null) {
        String path = dir.getAbsolutePath();
        if (!observableItems.contains(path)) {
          observableItems.add(path);
        }
      }
    });

    removeDirectoryButton.setOnAction(e -> {
      String selected = directoryListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        observableItems.remove(selected);
      }
    });

    closeButton.setOnAction(e -> this.close());
  }

  public void configure(String preferenceKey) {
    this.currentPreferenceKey = preferenceKey;

    observableItems = FXCollections.observableArrayList(
        this.getPreferences().getList(preferenceKey, new ArrayList<>()));

    observableItems.addListener((ListChangeListener<String>) change -> {
      this.getPreferences().putList(currentPreferenceKey, new ArrayList<>(observableItems));
      publisher.publishEvent(new PreferencesChangedEvent());
    });

    directoryListView.setItems(observableItems);
  }

  protected DialogLayout getLayout() {
    DialogLayout layout = new DialogLayout();
    Label title = new Label("Additional Directories");
    title.setGraphic(new FontIcon("mdi2f-folder-multiple"));
    layout.setHeading(title);
    layout.setBody(lazyViewRegistry.get(LazyViewRegistry.LIST_DIRECTORY_VIEW));
    return layout;
  }

}