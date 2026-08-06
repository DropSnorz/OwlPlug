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

package com.owlplug.plugin.controllers;

import com.owlplug.core.components.ApplicationDefaults.Prefs;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.plugin.components.PluginFilterState;
import com.owlplug.plugin.controllers.dialogs.DisablePluginDialogController;
import com.owlplug.plugin.model.IPlugin;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginComponent;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.model.PluginState;
import com.owlplug.plugin.services.PluginService;
import com.owlplug.plugin.ui.PluginKindBadgeView;
import com.owlplug.plugin.ui.PluginStateView;
import java.util.function.Predicate;
import java.io.File;
import com.owlplug.core.utils.Async;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

@Controller
public class PluginTableController extends BaseController {

  @Autowired
  @Lazy
  private PluginsController pluginsController;
  @Autowired
  private DisablePluginDialogController disableController;
  @Autowired
  private PluginService pluginService;

  private final SimpleStringProperty search = new SimpleStringProperty();
  private final SimpleObjectProperty<Predicate<IPlugin>> filterPredicate = new SimpleObjectProperty<>();
  private final TableView<IPlugin> tableView;

  private final ObservableList<IPlugin> pluginList;


  public PluginTableController() {
    tableView = new TableView<>();
    VBox.setVgrow(tableView, Priority.ALWAYS);

    createColumns();

    tableView.setRowFactory(tv -> {
      TableRow<IPlugin> row = new TableRow<>();
      row.itemProperty().addListener((obs, oldItem, newItem) -> {
        if (newItem != null) {
          row.setContextMenu(createPluginContextMenu(newItem));
        }
      });
      return row;
    });


    pluginList = FXCollections.observableArrayList();
    // Wraps an ObservableList and filters its content using the provided Predicate.
    // All changes in the ObservableList are propagated immediately to the FilteredList.
    FilteredList<IPlugin> filteredPluginList = new FilteredList<>(pluginList);

    filteredPluginList.predicateProperty().bind(Bindings.createObjectBinding(() -> {
      String searchVal = search.getValue();
      boolean hasSearch = searchVal != null && !searchVal.isEmpty();
      Predicate<IPlugin> fp = filterPredicate.get();
      if (!hasSearch && fp == null) {
        return null;
      }
      return plugin -> {
        boolean searchMatch = !hasSearch
            || plugin.getName().toLowerCase().contains(searchVal.toLowerCase())
            || (plugin.getCategory() != null
                && plugin.getCategory().toLowerCase().contains(searchVal.toLowerCase()));
        return searchMatch && (fp == null || fp.test(plugin));
      };
    }, search, filterPredicate));

    SortedList<IPlugin> sortedPluginList = new SortedList<>(filteredPluginList);
    tableView.setItems(sortedPluginList);
    sortedPluginList.comparatorProperty().bind(tableView.comparatorProperty());

  }

  private void createColumns() {
    TableColumn<IPlugin, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    TableColumn<IPlugin, PluginFormat> formatColumn = new TableColumn<>("Format");
    formatColumn.setCellValueFactory(
        cellData -> new SimpleObjectProperty<>(cellData.getValue().asPlugin().getFormat()));
    formatColumn.setCellFactory(e -> new TableCell<>() {
      @Override
      public void updateItem(PluginFormat item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setGraphic(null);
        } else {
          setGraphic(new PluginKindBadgeView(getTableRow().getItem(), getApplicationDefaults()));
        }
      }
    });
    TableColumn<IPlugin, String> manufacturerColumn = new TableColumn<>("Manufacturer");
    manufacturerColumn.setCellValueFactory(cellData ->
                                               new SimpleStringProperty(cellData.getValue().getManufacturerName()));
    TableColumn<IPlugin, String> versionColumn = new TableColumn<>("Version");
    versionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVersion()));
    TableColumn<IPlugin, String> categoryColumn = new TableColumn<>("Category");
    categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
    // Directory Column
    TableColumn<IPlugin, String> directoryColumn = new TableColumn<>("Directory");
    directoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
        FileUtils.getParentDirectoryName(cellData.getValue().asPlugin().getPath())));
    directoryColumn.setCellFactory(e -> new TableCell<>() {
      @Override
      public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
          setGraphic(null);
        } else {
          setText(item);
          setGraphic(new FontIcon("mdi2f-folder"));
        }
      }
    });
    // Scan Directory Column
    TableColumn<IPlugin, String> scanDirectoryColumn = new TableColumn<>("Scan Dir.");
    scanDirectoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
        FileUtils.getFilename(cellData.getValue().asPlugin().getScanDirectoryPath())));
    scanDirectoryColumn.setCellFactory(e -> new TableCell<>() {
      @Override
      public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText(null);
          setGraphic(null);
        } else {
          setText(item);
          setGraphic(new FontIcon("mdi2f-folder-search"));
        }
      }
    });
    // Plugin State Column
    TableColumn<IPlugin, PluginState> stateColumn = new TableColumn<>("State");
    stateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
        pluginService.getPluginState(cellData.getValue().asPlugin())));
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

  }

  public void bindFilterState(PluginFilterState filterState) {
    filterPredicate.bind(filterState.predicateProperty());
  }

  public void setPlugins(Iterable<Plugin> plugins) {
    pluginList.clear();
    plugins.forEach(p -> {
      pluginList.add(p);
      if (p.getComponents().size() > 1) {
        pluginList.addAll(p.getComponents());
      }
    });

  }

  public TableView<IPlugin> getTableView() {
    return tableView;
  }

  public void setNodeManaged(boolean isManaged) {
    this.tableView.setManaged(isManaged);
    this.tableView.setVisible(isManaged);
  }

  public void selectPluginById(long id) {
    for (IPlugin p : pluginList) {
      Plugin plugin = p.asPlugin(); // Get plugin or component parent
      if (plugin.getId().equals(id)) {
        tableView.getSelectionModel().select(plugin);
        break;
      }
    }
  }

  /**
   * Selects the table row for the given component id. Components only appear as
   * their own table row when the parent plugin has more than one component
   * (shell plugins); otherwise the parent plugin row is selected instead.
   */
  public void selectComponentById(long id) {
    IPlugin fallback = null;

    for (IPlugin p : pluginList) {
      if (p instanceof PluginComponent tableComponent && tableComponent.getId().equals(id)) {
        tableView.getSelectionModel().select(p);
        return;
      }
      if (p instanceof Plugin plugin && plugin.getComponents().stream().anyMatch(c -> c.getId().equals(id))) {
        fallback = p;
      }
    }

    if (fallback != null) {
      tableView.getSelectionModel().select(fallback);
    }
  }

  public SimpleStringProperty searchProperty() {
    return this.search;
  }

  public void refresh() {
    tableView.refresh();
  }

  private ContextMenu createPluginContextMenu(IPlugin plugin) {

    ContextMenu menu = new ContextMenu();
    MenuItem openDirItem = new MenuItem("Reveal in File Explorer");
    openDirItem.setOnAction(e -> {
      File pluginFile = new File(plugin.asPlugin().getPath());
      PlatformUtils.openFromDesktop(pluginFile.getParentFile());
    });

    menu.getItems().addAll(openDirItem, new SeparatorMenuItem());

    if (plugin instanceof Plugin p) {
      if (p.isDisabled()) {
        MenuItem enableItem = new MenuItem("Enable plugin");
        enableItem.setOnAction(e -> {
          Async.run(() -> pluginService.enablePlugin(p));
        });
        menu.getItems().add(enableItem);
      } else {
        MenuItem disableItem = new MenuItem("Disable plugin");
        disableItem.setOnAction(e -> {
          if (this.getPreferences().getBoolean(Prefs.App.SHOW_DIALOG_DISABLE_PLUGIN, true)) {
            this.disableController.setPlugin(p);
            this.disableController.show();
          } else {
            this.disableController.disablePluginWithoutPrompt(p);
          }
        });
        menu.getItems().add(disableItem);
      }

      menu.getItems().add(new SeparatorMenuItem());
    }

    MenuItem infoDisplayItem = new MenuItem("Toggle info display");
    menu.getItems().add(infoDisplayItem);
    infoDisplayItem.setOnAction(e -> {
      pluginsController.toggleInfoPaneDisplay();
    });

    return menu;
  }

}
