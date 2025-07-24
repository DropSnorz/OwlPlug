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

import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginFormat;
import java.io.File;

public class Vst3DllFile extends PluginFile {

  /**
   * Type checking function against current format for the given file.
   * @param file - file to test
   * @return true if the file matches the current file format
   */
  public static boolean formatCheck(File file) {
    return (file.getAbsolutePath().endsWith(".vst3") || file.getAbsolutePath().endsWith(".vst3.disabled"))
        && file.isFile();
    
  }
  
  Vst3DllFile(File file) {
    super(file);
  }

  @Override
  public Plugin toPlugin() {
    
    Plugin plugin = createPlugin();
    plugin.setFormat(PluginFormat.VST3);
    
    return plugin;
    
  }

}
