package com.owlplug.core.tasks.plugins.discovery.fileformats;

import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginFormat;
import java.io.File;
import org.apache.commons.io.FilenameUtils;

public class SoVstFile extends PluginFile {

  /**
   * Type checking function against current format for the given file.
   * @param file - file to test
   * @return true if the file matches the current file format
   */
  public static boolean formatCheck(File file) {
    return (file.getAbsolutePath().endsWith(".so") || file.getAbsolutePath().endsWith(".so.disabled"))
      && file.isFile();

  }

  public SoVstFile(File pluginFile) {
    super(pluginFile);
  }

  @Override
  public Plugin toPlugin() {
    String pluginName = FilenameUtils.removeExtension(this.getPluginFile().getName());
    String pluginPath = this.getPluginFile().getAbsolutePath().replace("\\", "/");
    Plugin plugin = new Plugin(pluginName, pluginPath, PluginFormat.VST2);

    plugin.setDisabled(this.isDisabled());

    return plugin;
  }
}
