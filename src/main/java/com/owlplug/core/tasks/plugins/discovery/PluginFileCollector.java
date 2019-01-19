package com.owlplug.core.tasks.plugins.discovery;

import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.platform.RuntimePlatform;
import com.owlplug.core.tasks.plugins.discovery.fileformats.PluginFile;
import com.owlplug.core.tasks.plugins.discovery.fileformats.PluginFileFormatResolver;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
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
      List<File> baseFiles = (List<File>) FileUtils.listFilesAndDirs(dir, TrueFileFilter.TRUE, TrueFileFilter.TRUE);
      PluginFileFormatResolver pluginFileResolver = new PluginFileFormatResolver(runtimePlatform, pluginFormat);

      for (File file : baseFiles) {
        PluginFile pluginFile = pluginFileResolver.resolve(file);
        if (pluginFile != null) {
          fileList.add(pluginFile);
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
