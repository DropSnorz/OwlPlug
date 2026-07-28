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

package com.owlplug.project.tasks.discovery;

import com.owlplug.core.utils.FileUtils;
import java.io.File;

public final class BackupFileDetector {

  private BackupFileDetector() {
  }

  /**
   * Generic, DAW-agnostic backup file heuristic used as a shared fallback by all
   * ProjectExplorer implementations. A file is considered a backup if its file name
   * (case-insensitive) contains "backup" or ends with ".bak", or if any of its path
   * segments is named (case-insensitive) "Backup" or "Backups".
   *
   * @param file candidate file
   * @return true if the file matches the generic backup pattern
   */
  public static boolean isGenericBackupFile(File file) {
    String fileName = file.getName().toLowerCase();
    if (fileName.contains("backup") || fileName.endsWith(".bak")) {
      return true;
    }

    String normalizedPath = FileUtils.convertPath(file.getAbsolutePath());
    for (String segment : normalizedPath.split("/")) {
      if (segment.equalsIgnoreCase("backup") || segment.equalsIgnoreCase("backups")) {
        return true;
      }
    }
    return false;
  }
}
