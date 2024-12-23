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
import org.springframework.stereotype.Controller;

@Controller
public class PluginTableController extends BaseController {

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

    tableView.getColumns().addAll(formatColumn, nameColumn, versionColumn, manufacturerColumn, categoryColumn);

    pluginList = FXCollections.observableArrayList();
    tableView.setItems(pluginList);

  }

  public void setPlugins(Iterable<Plugin> plugins) {
    pluginList.removeAll();
    plugins.forEach(pluginList::add);
  }

  public TableView<Plugin> getTableView() {
    return tableView;

  }

  public void refresh() {
    tableView.refresh();
  }


}



