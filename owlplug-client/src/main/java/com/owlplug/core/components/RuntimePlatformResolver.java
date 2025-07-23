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

package com.owlplug.core.components;

import com.owlplug.core.model.OperatingSystem;
import com.owlplug.core.model.RuntimePlatform;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RuntimePlatformResolver {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private Set<RuntimePlatform> platforms = new HashSet<>();
  private RuntimePlatform currentPlatform;

  public RuntimePlatformResolver() {

    RuntimePlatform winX86 = new RuntimePlatform("win-x32", OperatingSystem.WIN, "x32", new String[]{"win32"});
    platforms.add(winX86);
    RuntimePlatform winX64 = new RuntimePlatform("win-x64", OperatingSystem.WIN, "x64", new String[]{"win64"});
    platforms.add(winX64);
    RuntimePlatform mac = new RuntimePlatform("mac", OperatingSystem.MAC, "x64");
    platforms.add(mac);
    RuntimePlatform linuxX86 = new RuntimePlatform("linux-x32", OperatingSystem.LINUX, "x32", new String[]{"linux32"});
    platforms.add(linuxX86);
    RuntimePlatform linuxX64 = new RuntimePlatform("linux-x64", OperatingSystem.LINUX, "x64", new String[]{"linux64"});
    platforms.add(linuxX64);
    RuntimePlatform linuxArm86 = new RuntimePlatform("linux-arm32", OperatingSystem.LINUX, "arm32");
    platforms.add(linuxArm86);
    RuntimePlatform linuxArm64 = new RuntimePlatform("linux-arm64", OperatingSystem.LINUX, "arm64");
    platforms.add(linuxArm64);

    winX64.getCompatiblePlatforms().add(winX86);
    linuxX64.getCompatiblePlatforms().add(linuxX86);
    linuxArm64.getCompatiblePlatforms().add(linuxArm86);

    currentPlatform = resolve();
    log.info("Runtime Platform Resolved: " + currentPlatform.toString());
  }

  public RuntimePlatform resolve() {

    OperatingSystem os = resolveOperatingSystem();
    String arch = resolveArchitecture();

    Optional<RuntimePlatform> platform = platforms.stream()
            .filter(p -> p.getOperatingSystem().equals(os))
            .filter(p -> p.getArch().equals(arch))
            .findFirst();

    return platform.orElseGet(() -> new RuntimePlatform("unknown", os, arch));
  }

  public RuntimePlatform getCurrentPlatform() {
    return currentPlatform;
  }

  private String resolveArchitecture() {
    String arch = System.getProperty("os.arch");
    if (arch == null) {
      return "Unknown";
    }

    arch = arch.toLowerCase();

    if (arch.matches("^(amd64|x86_64)$")) {
      return "x64";
    } else if (arch.matches("^(i[3-6]86|x86)$")) {
      return "x32";
    } else if (arch.matches("^(aarch64|arm64)$")) {
      return "arm64";
    } else if (arch.matches("^(arm|arm32)$")) {
      return "arm32";
    } else {
      return "unknown";
    }
  }

  private OperatingSystem resolveOperatingSystem() {
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
