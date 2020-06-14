/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

public enum ApplicationState {
  
  RUNNING("RUNNING"),
  TERMINATED("TERMINATED"),
  UNKNOWN("UNKNOWN");
  
  private String text;

  ApplicationState(String text) {
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
  public static ApplicationState fromString(String text) {
    for (ApplicationState b : ApplicationState.values()) {
      if (b.text.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }

}
