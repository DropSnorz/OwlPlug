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
import com.owlplug.plugin.model.IPlugin;
import com.owlplug.plugin.model.PluginComponent;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class PluginKindBadgeView extends HBox {

  public PluginKindBadgeView(IPlugin plugin, ApplicationDefaults applicationDefaults) {
    super(4);
    setAlignment(Pos.CENTER_LEFT);

    if (plugin instanceof PluginComponent component) {
      getChildren().add(new PluginFormatBadgeView(component.asPlugin().getFormat(), applicationDefaults));
      FontIcon icon = new FontIcon("mdi2t-toy-brick-outline");
      Tooltip.install(icon, new Tooltip("Component"));
      getChildren().add(icon);
    } else {
      getChildren().add(new PluginFormatBadgeView(plugin.asPlugin().getFormat(), applicationDefaults));
    }
  }
}