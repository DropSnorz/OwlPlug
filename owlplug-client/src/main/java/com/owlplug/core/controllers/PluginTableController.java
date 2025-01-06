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

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.controllers.dialogs.DisablePluginDialogController;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.PluginState;
import com.owlplug.core.services.PluginService;
import com.owlplug.core.ui.PluginStateView;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.core.utils.PlatformUtils;
import java.io.File;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PluginTableController extends BaseController {

  @Autowired
  private PluginsController pluginsController;
  @Autowired
  private DisablePluginDialogController disableController;
  @Autowired
  private PluginService pluginService;

  private final SimpleStringProperty search = new SimpleStringProperty();
  private final TableView<Plugin> tableView;

  private final ObservableList<Plugin> pluginList;


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

    tableView.setRowFactory(tv -> {
      TableRow<Plugin> row = new TableRow<>();
      row.itemProperty().addListener((obs, oldItem, newItem) -> {
        if (newItem != null) {
          row.setContextMenu(createPluginContextMenu(newItem));
        }
      });
      return row;
    });
    tableView.setContextMenu(createPluginContextMenu(new Plugin()));


    pluginList = FXCollections.observableArrayList();
    // Wraps an ObservableList and filters its content using the provided Predicate.
    // All changes in the ObservableList are propagated immediately to the FilteredList.
    FilteredList<Plugin> filteredPluginList = new FilteredList<>(pluginList);

    filteredPluginList.predicateProperty().bind(Bindings.createObjectBinding(() -> {
      if (search.getValue() == null || search.getValue().isEmpty()) {
        return null;
      }
      return (plugin) -> plugin.getName().toLowerCase().contains(search.getValue().toLowerCase())
                 || (plugin.getCategory() != null && plugin.getCategory().toLowerCase().contains(search.getValue().toLowerCase()));
    }, search));

    tableView.setItems(filteredPluginList);

  }

  public void setPlugins(Iterable<Plugin> plugins) {
    pluginList.clear();
    plugins.forEach(pluginList::add);
  }

  public TableView<Plugin> getTableView() {
    return tableView;
  }

  public void setNodeManaged(boolean isManaged) {
    this.tableView.setManaged(isManaged);
    this.tableView.setVisible(isManaged);
  }

  public SimpleStringProperty searchProperty() {
    return this.search;
  }

  public void refresh() {
    tableView.refresh();
  }

  private ContextMenu createPluginContextMenu(Plugin plugin) {

    ContextMenu menu = new ContextMenu();
    MenuItem openDirItem = new MenuItem("Reveal in File Explorer");
    openDirItem.setOnAction(e -> {
        File pluginFile = new File(plugin.getPath());
        PlatformUtils.openFromDesktop(pluginFile.getParentFile());
    });

    menu.getItems().addAll(openDirItem, new SeparatorMenuItem());

    if (plugin.isDisabled()) {
      MenuItem enableItem = new MenuItem ("Enable plugin");
      enableItem.setOnAction(e -> {
        pluginService.enablePlugin(plugin);
        pluginsController.refresh();
      });
      menu.getItems().add(enableItem);
    } else {
      MenuItem disableItem = new MenuItem("Disable plugin");
      disableItem.setOnAction(e -> {
        if (this.getPreferences().getBoolean(ApplicationDefaults.SHOW_DIALOG_DISABLE_PLUGIN_KEY, true)) {
          this.disableController.setPlugin(plugin);
          this.disableController.show();
        } else {
          this.disableController.disablePluginWithoutPrompt(plugin);
        }
      });
      menu.getItems().add(disableItem);
    }

    menu.getItems().add(new SeparatorMenuItem());

    MenuItem infoDisplayItem = new MenuItem("Toggle info display");
    menu.getItems().add(infoDisplayItem);
    infoDisplayItem.setOnAction(e -> {
      pluginsController.toggleInfoPaneDisplay();
    });

    return menu;
  }

}
