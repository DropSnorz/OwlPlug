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
import com.owlplug.plugin.model.PluginFormat;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class PluginFormatBadgeView extends HBox {

  public enum DisplayMode {
    DEFAULT, ICON_ONLY, TEXT_ONLY
  }

  public PluginFormatBadgeView(String formatValue, ApplicationDefaults applicationDefaults) {
    this(PluginFormat.fromBundleString(formatValue), formatValue, applicationDefaults, DisplayMode.DEFAULT);
  }

  public PluginFormatBadgeView(String formatValue, ApplicationDefaults applicationDefaults, DisplayMode mode) {
    this(PluginFormat.fromBundleString(formatValue), formatValue, applicationDefaults, mode);
  }

  public PluginFormatBadgeView(PluginFormat format, ApplicationDefaults applicationDefaults) {
    this(format, format != null ? format.getName() : "?", applicationDefaults, DisplayMode.DEFAULT);
  }

  public PluginFormatBadgeView(PluginFormat format, ApplicationDefaults applicationDefaults, DisplayMode mode) {
    this(format, format != null ? format.getName() : "?", applicationDefaults, mode);
  }

  private PluginFormatBadgeView(PluginFormat format, String displayText, ApplicationDefaults applicationDefaults,
                                DisplayMode mode) {
    super(4);
    setAlignment(Pos.CENTER);
    getStyleClass().add("plugin-format-badge");

    String formatClass = format != null
        ? "plugin-format-badge-" + format.name().toLowerCase()
        : "plugin-format-badge-default";
    getStyleClass().add(formatClass);

    if (mode != DisplayMode.TEXT_ONLY) {
      ImageView iconView = new ImageView(applicationDefaults.getPluginFormatIcon(format));
      iconView.setFitHeight(16);
      iconView.setFitWidth(16);
      iconView.setPreserveRatio(true);
      getChildren().add(iconView);
    }

    if (mode != DisplayMode.ICON_ONLY) {
      Label label = new Label(displayText.toUpperCase());
      getChildren().add(label);
    }

    Tooltip.install(this, new Tooltip(displayText));
  }
}