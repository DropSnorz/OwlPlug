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

package com.owlplug.explore.model;

public enum SourceType {

  OWLPLUG_REGISTRY("owlplug-registry", "OwlPlug Registry", "R"),
  OWLPLUG_STORE("owlplug-store", "OwlPlug Store", "S");

  private String value;
  private String label;
  private String shortLabel;

  SourceType(String value, String label, String shortLabel) {
    this.value = value;
    this.label = label;
    this.shortLabel = shortLabel;
  }

  public String getValue() {
    return value;
  }

  public String getLabel() {
    return label;
  }

  public String getShortLabel() {
    return shortLabel;
  }
}
