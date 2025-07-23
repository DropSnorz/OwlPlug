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
 
package com.owlplug.plugin.tasks.discovery;

import com.owlplug.plugin.model.Symlink;
import com.owlplug.core.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SymlinkCollector {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  
  private boolean uniqueReferences;
  private Set<String> collectedSymlinks;

  public SymlinkCollector(boolean uniqueReferences) {
    this.uniqueReferences = uniqueReferences;
    collectedSymlinks = new HashSet<String>();
  }

  public List<Symlink> collect(String directoryPath) {

    File dir = new File(directoryPath);
    ArrayList<Symlink> linkList = new ArrayList<>();
    if (dir.isDirectory()) {
      List<File> baseFiles = (List<File>) FileUtils.listUniqueFilesAndDirs(dir);

      for (File file : baseFiles) {
        if (Files.isSymbolicLink(file.toPath())) {
          Symlink link = new Symlink(FileUtils.convertPath(file.getAbsolutePath()), file.getName(), true);
          Path targetPath;
          try {
            targetPath = Files.readSymbolicLink(file.toPath());
            link.setTargetPath(com.owlplug.core.utils.FileUtils.convertPath(targetPath.toString()));
            link.setStale(!targetPath.toFile().exists());
          } catch (IOException e) {
            log.error("Error reading symlink properties: " + file.getPath(), e);
          }
          if (uniqueReferences && !collectedSymlinks.contains(file.getAbsolutePath())) {
            collectedSymlinks.add(file.getAbsolutePath());
            linkList.add(link);
          } else if (!uniqueReferences) {
            linkList.add(link);
          }

        }
      }
    }

    return linkList;
  }

}
