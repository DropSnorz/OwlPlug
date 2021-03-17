package com.owlplug.core.tasks.plugins.discovery.fileformats;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginFormat;

public class AuComponentFile extends PluginFile {
	
	public static boolean formatCheck(File file) {
		return file.getAbsolutePath().endsWith(".component") 
				&& file.isDirectory();

	}

	public AuComponentFile(File pluginFile) {
		super(pluginFile);
	}

	@Override
	public Plugin toPlugin() {
		String pluginName = FilenameUtils.removeExtension(this.getPluginFile().getName());
	    String pluginPath = this.getPluginFile().getAbsolutePath().replace("\\", "/");
	    Plugin plugin = new Plugin(pluginName, pluginPath, PluginFormat.AU);
	    
	    File plist = new File(this.getPluginFile().getAbsolutePath() + "/Contents/Info.plist");
	    if (plist.exists()) {
	      OsxPlistFile plistFile = new OsxPlistFile(plist);
	      plistFile.bindProperties(plugin);
	    }
	    
	    return plugin;
	}

}
