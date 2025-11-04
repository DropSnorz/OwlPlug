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

package com.owlplug.host.model;

import java.util.Objects;

public enum OS {

  AIX,
  FREEBSD,
  LINUX,
  MAC,
  OPENBSD,
  SOLARIS,
  WINDOWS,
  OTHER;

  private static final OS CURRENT_OS = parse(System.getProperty("os.name"));

  public static OS current() {
    return CURRENT_OS;
  }

  static OS parse(String osName) {
    if (Objects.isNull(osName) || osName.isBlank()) {
      return null;
    }
    String osTag = osName.toLowerCase();

    if (osTag.contains("aix")) {
      return AIX;
    } else if (osTag.contains("freebsd")) {
      return FREEBSD;
    } else if (osTag.contains("linux")) {
      return LINUX;
    } else if (osTag.contains("mac")) {
      return MAC;
    } else if (osTag.contains("openbsd")) {
      return OPENBSD;
    } else if (osTag.contains("sunos") || osTag.contains("solaris")) {
      return SOLARIS;
    } else if (osTag.contains("win")) {
      return WINDOWS;
    }
    return OTHER;
  }

  public boolean isCurrentOs() {
    return this == CURRENT_OS;
  }
}

