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
import com.owlplug.core.model.PluginComponent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class PluginComponentCellFactory implements Callback<ListView<PluginComponent>, ListCell<PluginComponent>> {

  private ApplicationDefaults applicationDefaults;

  public PluginComponentCellFactory(ApplicationDefaults applicationDefaults) {

    this.applicationDefaults = applicationDefaults;

  }

  @Override
  public ListCell<PluginComponent> call(ListView<PluginComponent> arg0) {

    return new ListCell<>() {
      @Override
      public void updateItem(PluginComponent plugin, boolean empty) {
        super.updateItem(plugin, empty);
        if (empty) {
          setText(null);
          setGraphic(null);
        } else {
          ImageView imageView = new ImageView();
          imageView.setImage(applicationDefaults.pluginComponentImage);
          setText(plugin.getName());
          setGraphic(imageView);
        }
      }
    };
  }
}

