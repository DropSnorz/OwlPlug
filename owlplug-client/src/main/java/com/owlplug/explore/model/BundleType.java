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

/**
 * Kind of file a {@link PackageBundle} points to. Archives are extracted by OwlPlug into a
 * plugin directory, installers are executables handed over to the operating system.
 */
public enum BundleType {

  ARCHIVE("Archive"),
  INSTALLER("Installer");

  private final String label;

  BundleType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  /**
   * Maps an Open Audio Stack file {@code type} value to a bundle type.
   *
   * @param oasType OAS file type ("archive", "installer")
   * @return matching bundle type, or null if the type is missing or unsupported
   */
  public static BundleType fromOasType(String oasType) {
    if (oasType == null) {
      return null;
    }
    return switch (oasType.trim().toLowerCase()) {
      case "archive" -> ARCHIVE;
      case "installer" -> INSTALLER;
      default -> null;
    };
  }
}
