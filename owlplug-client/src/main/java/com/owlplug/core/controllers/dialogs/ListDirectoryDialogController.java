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
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.OptionsController;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ListDirectoryDialogController extends AbstractDialogController implements ListChangeListener<String> {

  @Autowired
  private OptionsController optionsController;
  @Autowired
  private LazyViewRegistry lazyViewRegistry;

  @FXML
  private JFXButton addDirectoryButton;
  @FXML
  private ListView<String> directoryListView;

  private final String newDirectoryItem = "[New directory] (double-click to update)";
  private String currentPreferenceKey;
  private ObservableList<String> observableItems;

  public void initialize() {

    addDirectoryButton.setOnAction(e -> {
      observableItems.add(newDirectoryItem);
    });
    directoryListView.setCellFactory(TextFieldListCell.forListView());
  }

  public void configure (String preferenceKey) {
    this.currentPreferenceKey = preferenceKey;

    if (observableItems != null) {
      observableItems.removeListener(this);
    }

    List<String> items = this.getPreferences().getList(preferenceKey, new ArrayList<String>());
    observableItems = FXCollections.observableArrayList(items);

    observableItems.addListener(this);
    directoryListView.setItems(observableItems);
    directoryListView.refresh();
  }


  @Override
  public void onChanged(Change<? extends String> change) {

    ObservableList<? extends String> eventList = change.getList();
    // Remove blank entries in the backed list
    eventList.removeIf(x -> x.isBlank());

    // Create a new list and filter it before saving it to preferences
    List<String> prefList = new ArrayList<>(change.getList());
    prefList.removeIf(x -> x.isBlank() || x.equals(newDirectoryItem));
    this.getPreferences().putList(currentPreferenceKey, prefList);

    optionsController.refreshView();
  }


  @Override
  protected Node getBody() {
    return lazyViewRegistry.get(LazyViewRegistry.LIST_DIRECTORY_VIEW);
  }

  @Override
  protected Node getHeading() {
    return null;
  }

}
