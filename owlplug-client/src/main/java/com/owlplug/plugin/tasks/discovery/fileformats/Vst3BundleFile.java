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
 
package com.owlplug.plugin.tasks.discovery.fileformats;

import com.google.common.collect.Iterables;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginFormat;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vst3BundleFile extends PluginFile {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * Type checking function against current format for the given file.
   * @param file - file to test
   * @return true if the file matches the current file format
   */
  public static boolean formatCheck(File file) {
    return (file.getAbsolutePath().endsWith(".vst3") || file.getAbsolutePath().endsWith(".vst3.disabled"))
        && file.isDirectory();

  }

  public Vst3BundleFile(File pluginFile) {
    super(pluginFile);
  }

  @Override
  public Plugin toPlugin() {

    Plugin plugin = createPlugin();
    plugin.setFormat(PluginFormat.VST3);

    File plist = new File(this.getPluginFile().getAbsolutePath() + "/Contents/Info.plist");
    if (plist.exists()) {
      OsxPlistFile plistFile = new OsxPlistFile(plist);
      plistFile.bindProperties(plugin);
    }

    File snapshotDirectory = new File(this.getPluginFile().getAbsolutePath() + "/Contents/Resources/Snapshots");

    if (snapshotDirectory.exists()) {
      Collection<File> snapshots = FileUtils.listFiles(snapshotDirectory, 
          new WildcardFileFilter("*_snapshot.png"), null);

      if (!snapshots.isEmpty()) {
        File snapshot = Iterables.get(snapshots, 0);
        try {
          plugin.setScreenshotUrl(snapshot.toURI().toURL().toString());
        } catch (MalformedURLException e) {
          log.error("Could not determine snapshot url for plugin: " + plugin.getName() 
              + " using filepath: " + snapshot.getAbsolutePath(), e);
        }
      }
    }

    return plugin;
  }

}
