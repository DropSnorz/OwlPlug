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
     * Windows VST3 < 3.6.10 Library files 
     */
    if (runtimePlatform.getOperatingSystem().equals(OperatingSystem.WIN)
        && pluginFormat.equals(PluginFormat.VST3)
        && Vst3DllFile.formatCheck(file)) {
      return new Vst3DllFile(file);
    }
    
    /*
     * Osx vst2 plugin
     */
    if (runtimePlatform.getOperatingSystem().equals(OperatingSystem.MAC)
        && pluginFormat.equals(PluginFormat.VST3)
        && OsxVstFile.formatCheck(file)) {
      return new OsxVstFile(file);
    }
    
    
    /*
     * Windows and OSX vst3 plugin bunle
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
