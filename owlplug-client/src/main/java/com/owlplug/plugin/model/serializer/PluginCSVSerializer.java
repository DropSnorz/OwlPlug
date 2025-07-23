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

package com.owlplug.plugin.model.serializer;

import com.owlplug.plugin.model.Plugin;

public class PluginCSVSerializer {

  private static final String FIELD_SEPARATOR = ";";
  private static final String LINE_SEPARATOR = "\n";

  public String getHeader() {
    StringBuilder builder = new StringBuilder("");
    builder.append("name").append(FIELD_SEPARATOR);
    builder.append("descriptiveName").append(FIELD_SEPARATOR);
    builder.append("uid").append(FIELD_SEPARATOR);
    builder.append("manufacturerName").append(FIELD_SEPARATOR);
    builder.append("identifier").append(FIELD_SEPARATOR);
    builder.append("path").append(FIELD_SEPARATOR);
    builder.append("bundleId").append(FIELD_SEPARATOR);
    builder.append("version").append(FIELD_SEPARATOR);
    builder.append("disabled").append(FIELD_SEPARATOR);
    builder.append("format").append(FIELD_SEPARATOR);
    builder.append("type").append(FIELD_SEPARATOR);
    builder.append(LINE_SEPARATOR);
    return builder.toString();
  }

  public String serialize(Iterable<Plugin> plugins) {

    StringBuilder builder = new StringBuilder("");
    for (Plugin plugin : plugins) {
      builder.append(serialize(plugin)).append(LINE_SEPARATOR);
    }
    return builder.toString();

  }

  public String serialize(Plugin plugin) {
    StringBuilder builder = new StringBuilder("");
    builder.append(plugin.getName()).append(FIELD_SEPARATOR);
    builder.append(plugin.getDescriptiveName()).append(FIELD_SEPARATOR);
    builder.append(plugin.getUid()).append(FIELD_SEPARATOR);
    builder.append(plugin.getManufacturerName()).append(FIELD_SEPARATOR);
    builder.append(plugin.getIdentifier()).append(FIELD_SEPARATOR);
    builder.append(plugin.getPath()).append(FIELD_SEPARATOR);
    builder.append(plugin.getBundleId()).append(FIELD_SEPARATOR);
    builder.append(plugin.getVersion()).append(FIELD_SEPARATOR);
    builder.append(plugin.isDisabled()).append(FIELD_SEPARATOR);
    builder.append(plugin.getFormat()).append(FIELD_SEPARATOR);
    builder.append(plugin.getType()).append(FIELD_SEPARATOR);

    return builder.toString();
  }
}
