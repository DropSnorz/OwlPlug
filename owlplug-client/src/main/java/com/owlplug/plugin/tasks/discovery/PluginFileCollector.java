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

import com.owlplug.core.model.RuntimePlatform;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.tasks.discovery.fileformats.PluginFile;
import com.owlplug.plugin.tasks.discovery.fileformats.PluginFileFormatResolver;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginFileCollector {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private RuntimePlatform runtimePlatform;

  private List<PluginFile> collectedFiles = new ArrayList<>();

  public PluginFileCollector(RuntimePlatform runtimePlatform) {
    super();
    this.runtimePlatform = runtimePlatform;
  }

  /**
   * Collects plugins files on the current environment. Plugins are collected from the directoryPath and 
   * all nested subfolders.
   * @param directoryPath - path where plugin are retrieved
   * @param pluginFormat - format to retrieve
   * @return a list of {@link PluginFile}
   */
  public List<PluginFile> collect(String directoryPath, PluginFormat pluginFormat) {

    File dir = new File(directoryPath);

    if (dir.isDirectory()) {
      List<File> baseFiles = (List<File>) FileUtils.listUniqueFilesAndDirs(dir);
      PluginFileFormatResolver pluginFileResolver = new PluginFileFormatResolver(runtimePlatform, pluginFormat);
      baseFiles.sort(Comparator.comparing(File::getAbsolutePath));

      List<File> filteredFiles = baseFiles.stream()
                                     // Filter out HFS metadata files starting with "._"
                                     .filter(file -> !file.getName().startsWith("._"))
                                     .toList();

      for (File file : filteredFiles) {
        /*
         *  Lookup for nested plugins in bundles and prevent them from being referenced multiple times.
         *  For example a VST3 bundle file can contain a .vst3 file for windows, but we
         *  don't want it to be referenced as it's an internal package managed by the host.
         *  Maybe this should be refactored to recursively explore directories and directly prevent exploration of
         *  bundles subdirectories.
         */
        boolean nestedPluginDetected = false;
        for (PluginFile previouslyCollectedFile : collectedFiles) {
          if (file.getAbsolutePath().contains(previouslyCollectedFile.getPluginFile().getAbsolutePath())) {
            nestedPluginDetected = true;
            // Early loop exit upon nested plugin detection
            break;
          }
        }

        if (!nestedPluginDetected && !file.equals(dir)) {
          PluginFile pluginFile = pluginFileResolver.resolve(file);
          if (pluginFile != null) {
            pluginFile.setScanDirectory(dir);
            collectedFiles.add(pluginFile);
          }
        }
      }
    } else {
      log.error("Scan target is not a valid directory. 0 plugins have been collected from " + directoryPath);
    }

    return collectedFiles;
  }

  public RuntimePlatform getRuntimePlatform() {
    return runtimePlatform;
  }

  public void setRuntimePlatform(RuntimePlatform runtimePlatform) {
    this.runtimePlatform = runtimePlatform;
  }

}
