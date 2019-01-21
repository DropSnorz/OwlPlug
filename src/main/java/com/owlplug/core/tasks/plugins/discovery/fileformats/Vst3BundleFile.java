package com.owlplug.core.tasks.plugins.discovery.fileformats;

import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.VST3Plugin;
import java.io.File;
import org.apache.commons.io.FilenameUtils;

public class Vst3BundleFile extends PluginFile {
  
  /**
   * Type checking function against current format for the given file.
   * @param file - file to test
   * @return true if the file matches the current file format
   */
  public static boolean formatCheck(File file) {
    return file.getAbsolutePath().endsWith(".vst3") 
        && file.isDirectory();
    
  }

  public Vst3BundleFile(File pluginFile) {
    super(pluginFile);
  }

  @Override
  public Plugin toPlugin() {
    
    String pluginName = FilenameUtils.removeExtension(this.getPluginFile().getName());
    String pluginPath = this.getPluginFile().getAbsolutePath().replace("\\", "/");
    Plugin plugin = new VST3Plugin(pluginName, pluginPath);
    
    File plist = new File(this.getPluginFile().getAbsolutePath() + "/Contents/Info.plist");
    if (plist.exists()) {
      OsxPlistFile plistFile = new OsxPlistFile(plist);
      plistFile.bindProperties(plugin);
    }
    
    //TODO Retrieve package Resources
    
    return plugin;
  }

}
