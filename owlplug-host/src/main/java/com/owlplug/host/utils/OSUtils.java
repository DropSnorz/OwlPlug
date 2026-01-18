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

package com.owlplug.host.utils;

import com.owlplug.host.model.OS;
import java.nio.file.FileSystems;

public class OSUtils {

  public static String getPlatformTagName() {
    String osTag = getOSTagName();
    String archTag = getArchTagName();
    return osTag + "-" + archTag;
  }

  public static String getOSTagName() {
    if (OS.WINDOWS.isCurrentOs()) {
      return "win";
    } else if (OS.MAC.isCurrentOs()) {
      return "osx";
    } else if (OS.LINUX.isCurrentOs()) {
      return "linux";
    }
    return "unknown";
  }

  public static String getArchTagName() {
    String arch = System.getProperty("os.arch");
    if (arch == null) {
      return "unknown";
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

  public static boolean isPosixFileSystem() {
    return FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
  }

}
