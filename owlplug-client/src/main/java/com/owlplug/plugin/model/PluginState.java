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

public enum PluginState {

  INSTALLED("Installed", "Plugin has been detected by OwlPlug"),
  DISABLED("Disabled", "Plugin is disabled"),
  UNSTABLE("Unstable", "Last plugin scan crashed OwlPlug"),
  ACTIVE("Active", "Plugin is working properly");

  private final String text;
  private final String description;

  PluginState(String text, String description) {
    this.text = text;
    this.description = description;
  }

  public String getText() {
    return text;
  }

  public String getDescription() {
    return description;
  }
}
