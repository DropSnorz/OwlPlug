package com.owlplug.core.tasks.plugins.discovery.fileformats;

import com.google.common.collect.Iterables;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.VST3Plugin;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
