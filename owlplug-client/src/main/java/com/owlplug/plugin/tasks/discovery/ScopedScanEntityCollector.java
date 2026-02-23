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


import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.model.Symlink;
import com.owlplug.plugin.tasks.discovery.fileformats.PluginFile;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Collect plugins and symlinks based on task scan properties.
 */
public class ScopedScanEntityCollector {

  private final PluginScanTaskParameters parameters;

  private Set<PluginFile> pluginFiles;

  private Set<Symlink> symlinks;

  public ScopedScanEntityCollector(PluginScanTaskParameters parameters) {
    this.parameters = parameters;
  }

  public ScopedScanEntityCollector collect() {

    Set<PluginFile> collectedPluginFiles = new LinkedHashSet<>();
    PluginFileCollector pluginCollector = new PluginFileCollector(parameters.getPlatform());

    Set<Symlink> collectedSymlinks = new LinkedHashSet<>();
    SymlinkCollector symlinkCollector = new SymlinkCollector(true);


    if (parameters.getDirectoryScope() != null) {
      // Plugins are retrieved from a scoped directory
      if (parameters.isFindLv2()) {
        collectedPluginFiles.addAll(pluginCollector.collect(parameters.getDirectoryScope(), PluginFormat.LV2));
      }
      if (parameters.isFindVst3()) {
        collectedPluginFiles.addAll(pluginCollector.collect(parameters.getDirectoryScope(), PluginFormat.VST3));
      }
      if (parameters.isFindVst2()) {
        collectedPluginFiles.addAll(pluginCollector.collect(parameters.getDirectoryScope(), PluginFormat.VST2));
      }
      if (parameters.isFindAu()) {
        collectedPluginFiles.addAll(pluginCollector.collect(parameters.getDirectoryScope(), PluginFormat.AU));
      }

      collectedSymlinks.addAll(symlinkCollector.collect(parameters.getDirectoryScope()));

      // Try to restore original scan directory when a directory scope is used
      // https://github.com/DropSnorz/OwlPlug/issues/424
      String scanPath = getScanPathFromDirectoryScope(parameters.getDirectoryScope());
      if (scanPath != null) {
        File scanFile = new File(scanPath);
        collectedPluginFiles.forEach(f -> f.setScanDirectory(scanFile));
      }


    } else {
      // Plugins are retrieved from regulars directories
      String vst2Directory = parameters.getVst2Directory();
      String vst3Directory = parameters.getVst3Directory();
      String auDirectory = parameters.getAuDirectory();
      String lv2Directory = parameters.getLv2Directory();

      if (parameters.isFindLv2()) {
        collectedPluginFiles.addAll(pluginCollector.collect(lv2Directory, PluginFormat.LV2));
        collectedSymlinks.addAll(symlinkCollector.collect(lv2Directory));
        for (String path : parameters.getLv2ExtraDirectories()) {
          collectedPluginFiles.addAll(pluginCollector.collect(path, PluginFormat.LV2));
          collectedSymlinks.addAll(symlinkCollector.collect(path));
        }
      }

      if (parameters.isFindVst3()) {
        collectedPluginFiles.addAll(pluginCollector.collect(vst3Directory, PluginFormat.VST3));
        collectedSymlinks.addAll(symlinkCollector.collect(vst3Directory));
        for (String path : parameters.getVst3ExtraDirectories()) {
          collectedPluginFiles.addAll(pluginCollector.collect(path, PluginFormat.VST3));
          collectedSymlinks.addAll(symlinkCollector.collect(path));
        }
      }

      if (parameters.isFindVst2()) {
        collectedPluginFiles.addAll(pluginCollector.collect(vst2Directory, PluginFormat.VST2));
        collectedSymlinks.addAll(symlinkCollector.collect(vst2Directory));
        for (String path : parameters.getVst2ExtraDirectories()) {
          collectedPluginFiles.addAll(pluginCollector.collect(path, PluginFormat.VST2));
          collectedSymlinks.addAll(symlinkCollector.collect(path));
        }
      }

      if (parameters.isFindAu()) {
        collectedPluginFiles.addAll(pluginCollector.collect(auDirectory, PluginFormat.AU));
        collectedSymlinks.addAll(symlinkCollector.collect(auDirectory));
        for (String path : parameters.getAuExtraDirectories()) {
          collectedPluginFiles.addAll(pluginCollector.collect(path, PluginFormat.AU));
          collectedSymlinks.addAll(symlinkCollector.collect(path));
        }
      }
    }
    this.pluginFiles = collectedPluginFiles;
    this.symlinks = collectedSymlinks;

    return this;
  }

  public Set<PluginFile> getPluginFiles() {
    return pluginFiles;
  }

  public Set<Symlink> getSymlinks() {
    return symlinks;
  }

  private Set<String> getAllPluginScanPath() {
    Set<String> paths = new HashSet<String>();

    paths.add(parameters.getVst2Directory());
    paths.add(parameters.getVst3Directory());
    paths.add(parameters.getAuDirectory());
    paths.add(parameters.getLv2Directory());

    paths.addAll(parameters.getVst2ExtraDirectories());
    paths.addAll(parameters.getVst3ExtraDirectories());
    paths.addAll(parameters.getAuExtraDirectories());
    paths.addAll(parameters.getLv2ExtraDirectories());

    // Clean null and blank paths if some are provided
    paths.removeIf(p -> p == null || p.isBlank());

    return paths;

  }

  /**
   * Compute original scan directory based on the provided directory scope.
   * See related <a href="https://github.com/DropSnorz/OwlPlug/issues/424">issue</a>
   * @param directoryScope the directory scope
   * @return path to the potential scan directory
   */
  private String getScanPathFromDirectoryScope(String directoryScope) {
    return getAllPluginScanPath()
               .stream()
               .sorted()
               .filter(s -> directoryScope.startsWith(s))
               .findFirst().orElse(null);
  }

}
