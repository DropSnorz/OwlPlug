package com.dropsnorz.owlplug.services;

import java.io.File;
import java.nio.file.Files;

import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.model.Plugin;

@Service
public class PluginService {
	
	public boolean removePlugin(Plugin plugin) {
	
		File pluginFile = new File(plugin.getPath());
		
		return pluginFile.delete();
		
	}

}
