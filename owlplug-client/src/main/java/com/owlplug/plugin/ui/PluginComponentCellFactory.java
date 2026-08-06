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

import com.owlplug.plugin.model.PluginComponent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

public class PluginComponentCellFactory implements Callback<ListView<PluginComponent>, ListCell<PluginComponent>> {


  public PluginComponentCellFactory() {

  }

  @Override
  public ListCell<PluginComponent> call(ListView<PluginComponent> arg0) {
    return new ListCell<>() {
      @Override
      public void updateItem(PluginComponent component, boolean empty) {
        super.updateItem(component, empty);
        if (empty) {
          setText(null);
          setGraphic(null);
        } else {
          FontIcon icon;
          if (component.isActive()) {
            icon = new FontIcon("mdi2t-toy-brick-outline");
            setText(component.getName());
          } else {
            icon = new FontIcon("mdi2t-toy-brick-remove-outline");
            setText(component.getName() + " (Unscannable)");
          }
          setGraphic(icon);
        }
      }
    };
  }
}

