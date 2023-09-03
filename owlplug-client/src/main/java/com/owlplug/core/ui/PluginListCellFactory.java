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
 
package com.owlplug.core.ui;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.model.Plugin;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class PluginListCellFactory implements Callback<ListView<Plugin>, ListCell<Plugin>> {

  private ApplicationDefaults applicationDefaults;

  public PluginListCellFactory(ApplicationDefaults applicationDefaults) {

    this.applicationDefaults = applicationDefaults;
  }

  @Override
  public ListCell<Plugin> call(ListView<Plugin> arg0) {
    return new ListCell<Plugin>() {
      private ImageView imageView = new ImageView();

      @Override
      public void updateItem(Plugin plugin, boolean empty) {
        super.updateItem(plugin, empty);
        if (empty) {
          setText(null);
          setGraphic(null);
        } else {
          imageView.setImage(applicationDefaults.getPluginFormatIcon(plugin));
          setText(plugin.getName());
          setGraphic(imageView);
        }
      }
    };
  }

}
