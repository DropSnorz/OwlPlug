package com.dropsnorz.owlplug.services;

import java.io.File;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.repositories.PluginRepository;

@Service
public class PluginService {
	
	@Autowired
	PluginRepository pluginRepository;
	
	public boolean removePlugin(Plugin plugin) {
	
		pluginRepository.delete(plugin);
		File pluginFile = new File(plugin.getPath());
		if(pluginFile.delete()) {
			pluginRepository.delete(plugin);
			return true;
		}
		
		return false;
		
	}

}
