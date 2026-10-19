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

package com.owlplug.explore.model.mappers;

import com.owlplug.explore.model.PackageTag;
import com.owlplug.explore.model.RemotePackage;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PackageTagMapper {

  private PackageTagMapper() {
  }

  /**
   * Creates {@link PackageTag} entities from registry tag names. Names are trimmed, blank names
   * are dropped and duplicates (ignoring case) only keep their first occurrence.
   *
   * @param names         - tag names, can be null
   * @param remotePackage - related package
   * @return package tags
   */
  public static HashSet<PackageTag> toEntities(List<String> names, RemotePackage remotePackage) {
    HashSet<PackageTag> tags = new HashSet<>();
    if (names == null) {
      return tags;
    }
    Set<String> seen = new HashSet<>();
    for (String name : names) {
      if (name == null || name.isBlank()) {
        continue;
      }
      String trimmed = name.trim();
      if (seen.add(trimmed.toLowerCase(Locale.ROOT))) {
        tags.add(new PackageTag(trimmed, remotePackage));
      }
    }
    return tags;
  }
}
