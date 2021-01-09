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
 
package com.owlplug.core.tasks.plugins.discovery.fileformats;

import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.platform.OperatingSystem;
import com.owlplug.core.model.platform.RuntimePlatform;
import java.io.File;

public class PluginFileFormatResolver {
  
  private RuntimePlatform runtimePlatform;
  private PluginFormat pluginFormat;
  
  /**
   * Creates a new {@link PluginFileFormatResolver} instance.
   * @param runtimePlatform - current runtime environment
   * @param pluginFormat - plugin format to lookup
   */
  public PluginFileFormatResolver(RuntimePlatform runtimePlatform, PluginFormat pluginFormat) {
    this.runtimePlatform = runtimePlatform;
    this.pluginFormat = pluginFormat;
    
  }
  
  /**
   * Resolves {@link PluginFile} format for a given File.
   * @param file - file to analyse
   * @return the PluginFormat or null if format is undetermined
   */
  public PluginFile resolve(File file) {
    
    /*
     * Windows VST2 DLL files 
     */
    if (runtimePlatform.getOperatingSystem().equals(OperatingSystem.WIN)
        && pluginFormat.equals(PluginFormat.VST2)
        && VstDllFile.formatCheck(file)) {
      return new VstDllFile(file);
    }
    
    /*
     * Osx VST2 plugin
     */
    if (runtimePlatform.getOperatingSystem().equals(OperatingSystem.MAC)
        && pluginFormat.equals(PluginFormat.VST2)
        && OsxVstFile.formatCheck(file)) {
      return new OsxVstFile(file);
    }
    
    /*
     * Windows VST3 < 3.6.10 Library files 
     */
    if (runtimePlatform.getOperatingSystem().equals(OperatingSystem.WIN)
        && pluginFormat.equals(PluginFormat.VST3)
        && Vst3DllFile.formatCheck(file)) {
      return new Vst3DllFile(file);
    }
    
    /*
     * Windows and OSX VST3 plugin bundle
     */
    if ((runtimePlatform.getOperatingSystem().equals(OperatingSystem.MAC)
        || runtimePlatform.getOperatingSystem().equals(OperatingSystem.WIN))
        && pluginFormat.equals(PluginFormat.VST3)
        && Vst3BundleFile.formatCheck(file)) {
      return new Vst3BundleFile(file);
    }
    
    
    return null;
  }

}
