package com.owlplug.core.tasks.plugins.discovery.fileformats;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.VST2Plugin;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

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
    Plugin plugin = new VST2Plugin(pluginName, pluginPath);
    
    File plist = new File(this.getPluginFile().getAbsolutePath() + "/Contents/Info.plist");
    if (plist.exists()) {
      bindPlistInfo(plugin, plist);
    }
    
    return plugin;
    
  }
  
  private void bindPlistInfo(Plugin plugin, File plist) {
    try {
      NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(plist);
      NSObject nsBundleId = rootDict.objectForKey("CFBundleIdentifier");
      NSObject nsBundleVersion = rootDict.objectForKey("CFBun" + "dleShortVersionString");

      if (nsBundleVersion == null) {
        nsBundleVersion = rootDict.objectForKey("CFBundleVersion");
      }
      if (nsBundleId != null) {
        plugin.setBundleId(nsBundleId.toString());
      }
      if (nsBundleVersion != null) {
        plugin.setVersion(nsBundleVersion.toString());
      }
    } catch (IOException | PropertyListFormatException | ParseException | ParserConfigurationException
        | SAXException e) {
      log.error("Error while building plugin from Plistfile", e);
    }
  }
}
