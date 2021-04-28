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
 
package com.owlplug.core.model;

public enum PluginType {
  INSTRUMENT("instrument"), EFFECT("effect");

  private String text;

  PluginType(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }

  /**
   * Retrieves an enum instance matching a text string. Returns null if the given
   * string doesn't match any defined enum instance.
   * 
   * @param text enum unique text
   * @return
   */
  public static PluginType fromString(String text) {
    for (PluginType b : PluginType.values()) {
      if (b.text.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }

}
