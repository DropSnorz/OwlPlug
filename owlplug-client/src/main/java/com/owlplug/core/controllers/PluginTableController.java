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

package com.owlplug.core.controllers;

import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.PluginState;
import com.owlplug.core.services.PluginService;
import com.owlplug.core.ui.PluginStateView;
import com.owlplug.core.utils.FileUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PluginTableController extends BaseController {

  @Autowired
  private PluginService pluginService;

  private TableView<Plugin> tableView;

  private ObservableList<Plugin> pluginList;


  public PluginTableController() {
    tableView = new TableView<>();
    VBox.setVgrow(tableView, Priority.ALWAYS);

    TableColumn<Plugin, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    TableColumn<Plugin, PluginFormat> formatColumn = new TableColumn<>("Format");
    formatColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFormat()));
    formatColumn.setCellFactory(e -> new TableCell<>() {
      @Override
      public void updateItem(PluginFormat item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
          setGraphic(null);
        } else {
          setText(item.getText());
          setGraphic(new ImageView(getApplicationDefaults().getPluginFormatIcon(item)));
        }
      }
    });
    TableColumn<Plugin, String> manufacturerColumn = new TableColumn<>("Manufacturer");
    manufacturerColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getManufacturerName()));
    TableColumn<Plugin, String> versionColumn = new TableColumn<>("Version");
    versionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVersion()));
    TableColumn<Plugin, String> categoryColumn = new TableColumn<>("Category");
    categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
    // Directory Column
    TableColumn<Plugin, String> directoryColumn = new TableColumn<>("Directory");
    directoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
        FileUtils.getParentDirectoryName(cellData.getValue().getPath())));
    directoryColumn.setCellFactory(e -> new TableCell<>() {
      @Override
      public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
          setGraphic(null);
        } else {
          setText(item);
          setGraphic(new ImageView(getApplicationDefaults().directoryImage));
        }
      }
    });
    // Scan Directory Column
    TableColumn<Plugin, String> scanDirectoryColumn = new TableColumn<>("Scan Dir.");
    scanDirectoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
        FileUtils.getFilename(cellData.getValue().getScanDirectoryPath())));
    scanDirectoryColumn.setCellFactory(e -> new TableCell<>() {
      @Override
      public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
          setGraphic(null);
        } else {
          setText(item);
          setGraphic(new ImageView(getApplicationDefaults().scanDirectoryImage));
        }
      }
    });
    // Plugin State Column
    TableColumn<Plugin, PluginState> stateColumn = new TableColumn<>("State");
    stateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
        pluginService.getPluginState(cellData.getValue())));
    stateColumn.setCellFactory(e -> new TableCell<>() {
      @Override
      public void updateItem(PluginState item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
          setGraphic(null);
        } else {
          setGraphic(new PluginStateView(item));
        }
      }
    });

    tableView.getColumns().addAll(formatColumn, nameColumn, versionColumn,
        manufacturerColumn, categoryColumn, directoryColumn, scanDirectoryColumn, stateColumn);

    pluginList = FXCollections.observableArrayList();
    tableView.setItems(pluginList);

  }

  public void setPlugins(Iterable<Plugin> plugins) {
    pluginList.clear();
    plugins.forEach(pluginList::add);
  }

  public TableView<Plugin> getTableView() {
    return tableView;

  }

  public void refresh() {
    tableView.refresh();
  }


}



