package com.owlplug.core.tasks.plugins.discovery.fileformats;

import com.owlplug.core.model.Plugin;
import java.io.File;

public abstract class PluginFile {
  
  private File pluginFile;
  
  public PluginFile(File pluginFile) {
    this.pluginFile = pluginFile;
  }
  
  public abstract Plugin toPlugin();


  public File getPluginFile() {
    return pluginFile;
  }

  public void setPluginFile(File pluginFile) {
    this.pluginFile = pluginFile;
  }
  

}
