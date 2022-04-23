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

package com.owlplug.core.model.platform;

import java.util.HashMap;

public class RuntimePlatformResolver {

  private static HashMap<String, RuntimePlatform> platforms = new HashMap<>();

  static {
    RuntimePlatform win32 = new RuntimePlatform("win32", OperatingSystem.WIN, "32");
    platforms.put("win32", win32);
    RuntimePlatform win64 = new RuntimePlatform("win64", OperatingSystem.WIN, "64");
    platforms.put("win64", win64);
    RuntimePlatform osx = new RuntimePlatform("osx", OperatingSystem.MAC, "64");
    platforms.put("osx", osx);
    RuntimePlatform linux32 = new RuntimePlatform("linux32", OperatingSystem.LINUX, "32");
    platforms.put("linux32", linux32);
    RuntimePlatform linux64 = new RuntimePlatform("linux64", OperatingSystem.LINUX, "64");
    platforms.put("linux64", linux64);

    win64.getCompatiblePlatforms().add(win32);
    linux64.getCompatiblePlatforms().add(linux32);

  }

  public RuntimePlatform resolve() {

    String osName = System.getProperty("os.name").toLowerCase();

    // Windows
    if (osName.contains("win")) {
      if (is64bitPlatform()) {
        return platforms.get("win64");
      } else {
        return platforms.get("win32");
      }
    }

    // macOS
    if (osName.contains("mac")) {
      return platforms.get("osx");
    }

    // Linux
    if (osName.contains("nix") || osName.contains("nux")) {
      if (is64bitPlatform()) {
        return platforms.get("linux64");
      } else {
        return platforms.get("linux32");
      }
    }

    return new RuntimePlatform("unknown", OperatingSystem.UNKNOWN, "");
  }

  private boolean is64bitPlatform() {
    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
      return System.getenv("ProgramFiles(x86)") != null;
    } else {
      return System.getProperty("os.arch").contains("64");
    }
  }

}
