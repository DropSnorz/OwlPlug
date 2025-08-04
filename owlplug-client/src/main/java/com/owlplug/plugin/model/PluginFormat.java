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
 
package com.owlplug.plugin.model;

public enum PluginFormat {
  VST2("VST2"), VST3("VST3"), AU("AU"), LV2("LV2");

  private final String text;

  PluginFormat(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  /**
   * Retrieves an enum instance matching a text string. Returns null if the given
   * string doesn't match any defined enum instance.
   *
   * @param text enum unique text
   * @return
   */
  public static PluginFormat fromBundleString(String text) {
    if (text.equalsIgnoreCase("vst")) {
      return PluginFormat.VST2;
    }
    for (PluginFormat f : PluginFormat.values()) {
      if (f.text.equalsIgnoreCase(text)) {
        return f;
      }
    }
    return null;
  }

}
