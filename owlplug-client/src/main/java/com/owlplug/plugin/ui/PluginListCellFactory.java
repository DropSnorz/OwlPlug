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
 
package com.owlplug.plugin.ui;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.plugin.model.Plugin;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class PluginListCellFactory implements Callback<ListView<Plugin>, ListCell<Plugin>> {

  private ApplicationDefaults applicationDefaults;

  public PluginListCellFactory(ApplicationDefaults applicationDefaults) {

    this.applicationDefaults = applicationDefaults;
  }

  @Override
  public ListCell<Plugin> call(ListView<Plugin> arg0) {
    return new ListCell<>() {
      @Override
      public void updateItem(Plugin plugin, boolean empty) {
        super.updateItem(plugin, empty);
        if (empty) {
          setText(null);
          setGraphic(null);
        } else {
          setText(plugin.getName());
          setGraphic(new PluginFormatBadgeView(plugin.getFormat(), applicationDefaults,
              PluginFormatBadgeView.DisplayMode.ICON_ONLY));
        }
        // Force re-rendering immediately to avoid blinking cell
        applyCss();
      }
    };
  }

}
