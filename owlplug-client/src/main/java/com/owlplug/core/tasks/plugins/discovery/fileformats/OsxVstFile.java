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
 
package com.owlplug.core.tasks.plugins.discovery.fileformats;

import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginFormat;
import java.io.File;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsxVstFile extends PluginFile {
  
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * Type checking function against current format for the given file.
   * @param file - file to test
   * @return true if the file matches the current file format
   */
  public static boolean formatCheck(File file) {
    return file.getAbsolutePath().endsWith(".vst") 
        && file.isDirectory();
    
  }
  
  public OsxVstFile(File file) {
    super(file);
  }

  @Override
  public Plugin toPlugin() {
    
    String pluginName = FilenameUtils.removeExtension(this.getPluginFile().getName());
    String pluginPath = this.getPluginFile().getAbsolutePath().replace("\\", "/");
    Plugin plugin = new Plugin(pluginName, pluginPath, PluginFormat.VST2);
    
    File plist = new File(this.getPluginFile().getAbsolutePath() + "/Contents/Info.plist");
    if (plist.exists()) {
      OsxPlistFile plistFile = new OsxPlistFile(plist);
      plistFile.bindProperties(plugin);
    }
    
    return plugin;
    
  }
  
}
