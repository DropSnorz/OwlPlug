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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RuntimePlatformResolver {

  private static Set<RuntimePlatform> platforms = new HashSet<>();

  static {
    RuntimePlatform winX86 = new RuntimePlatform("win-x86", OperatingSystem.WIN, "x86", new String[]{"win32"});
    platforms.add(winX86);
    RuntimePlatform winX64 = new RuntimePlatform("win-x64", OperatingSystem.WIN, "x64", new String[]{"win64"});
    platforms.add(winX64);
    RuntimePlatform mac = new RuntimePlatform("mac", OperatingSystem.MAC, "x64");
    platforms.add(mac);
    RuntimePlatform linuxX86 = new RuntimePlatform("linux-x86", OperatingSystem.LINUX, "x86", new String[]{"linux32"});
    platforms.add(linuxX86);
    RuntimePlatform linuxX64 = new RuntimePlatform("linux-x64", OperatingSystem.LINUX, "x64", new String[]{"linux64"});
    platforms.add(linuxX64);
    RuntimePlatform linuxArm86 = new RuntimePlatform("linux-arm86", OperatingSystem.LINUX, "arm86");
    platforms.add(linuxArm86);
    RuntimePlatform linuxArm64 = new RuntimePlatform("linux-arm64", OperatingSystem.LINUX, "arm64");
    platforms.add(linuxArm64);

    winX64.getCompatiblePlatforms().add(winX86);
    linuxX64.getCompatiblePlatforms().add(linuxX86);
    linuxArm64.getCompatiblePlatforms().add(linuxArm86);

  }

  public RuntimePlatform resolve() {

    OperatingSystem os = getOperatingSystem();
    String arch = getArchitecture();

    Optional<RuntimePlatform> platform = platforms.stream()
            .filter(p -> p.getOperatingSystem().equals(os))
            .filter(p -> p.getArch().equals(arch))
            .findFirst();

    return platform.orElseGet(() -> new RuntimePlatform("unknown", os, arch));
  }

  private static String getArchitecture() {
    String arch = System.getProperty("os.arch");
    if (arch == null) {
      return "Unknown";
    }

    arch = arch.toLowerCase();

    if (arch.matches("^(amd64|x86_64)$")) {
      return "x64";
    } else if (arch.matches("^(i[3-6]86|x86)$")) {
      return "x86";
    } else if (arch.matches("^(aarch64|arm64)$")) {
      return "arm64";
    } else if (arch.matches("^(arm|arm32)$")) {
      return "arm32";
    } else {
      return "unknown";
    }
  }

  private OperatingSystem getOperatingSystem() {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win")) {
      return OperatingSystem.WIN;
    }
    if (osName.contains("mac")) {
      return OperatingSystem.MAC;
    }
    if (osName.contains("nix") || osName.contains("nux")) {
      return OperatingSystem.LINUX;
    }
    return OperatingSystem.UNKNOWN;
  }

}
