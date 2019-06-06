/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.core.tasks.plugins.discovery;

import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.platform.RuntimePlatform;
import com.owlplug.core.tasks.plugins.discovery.fileformats.PluginFile;
import com.owlplug.core.tasks.plugins.discovery.fileformats.PluginFileFormatResolver;
import com.owlplug.core.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginFileCollector {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private RuntimePlatform runtimePlatform;

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

    ArrayList<PluginFile> fileList = new ArrayList<>();
    File dir = new File(directoryPath);

    if (dir.isDirectory()) {
      List<File> baseFiles = (List<File>) FileUtils.listUniqueFilesAndDirs(dir);
      PluginFileFormatResolver pluginFileResolver = new PluginFileFormatResolver(runtimePlatform, pluginFormat);

      for (File file : baseFiles) {

        /*
         *  Loopkup for nested plugins in bundles and prevent them from being referenced multiple times.
         *  For example a VST3 bundle file can contains a .vst3 file for windows but we
         *  don't want it to be referenced as it's an internal package managed by the host.
         *  Maybe this should be refactored to recursively explore directories and directly prevent exploration of
         *  bundles subdirectories.
         */
        boolean nestedPluginDetected = false;
        for (PluginFile previouslyCollectedFile : fileList) {
          if (file.getAbsolutePath().contains(previouslyCollectedFile.getPluginFile().getAbsolutePath())) {
            nestedPluginDetected = true;
          }
        }

        if (!nestedPluginDetected) {
          PluginFile pluginFile = pluginFileResolver.resolve(file);
          if (pluginFile != null) {
            fileList.add(pluginFile);
          }
        }
      }
    } else {
      log.error("Plugin root is not a directory");
    }

    return fileList;
  }

  public RuntimePlatform getRuntimePlatform() {
    return runtimePlatform;
  }

  public void setRuntimePlatform(RuntimePlatform runtimePlatform) {
    this.runtimePlatform = runtimePlatform;
  }

}
