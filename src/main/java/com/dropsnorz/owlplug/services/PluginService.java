package com.dropsnorz.owlplug.services;

import java.io.File;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.dao.PluginDAO;
import com.dropsnorz.owlplug.model.Plugin;

@Service
public class PluginService {
	
	@Autowired
	PluginDAO pluginDAO;
	
	public boolean removePlugin(Plugin plugin) {
	
		File pluginFile = new File(plugin.getPath());
		if(pluginFile.delete()) {
			pluginDAO.delete(plugin);
			return true;
		}
		
		return false;
		
	}

}
