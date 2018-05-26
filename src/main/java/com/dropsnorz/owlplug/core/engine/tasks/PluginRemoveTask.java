package com.dropsnorz.owlplug.core.engine.tasks;

import java.io.File;

import com.dropsnorz.owlplug.core.dao.PluginDAO;
import com.dropsnorz.owlplug.core.model.Plugin;

import javafx.concurrent.Task;

public class PluginRemoveTask extends Task {

	protected Plugin plugin;
	protected PluginDAO pluginDAO;
	
	public PluginRemoveTask(Plugin plugin, PluginDAO pluginDAO){
		
		this.plugin = plugin;
		this.pluginDAO = pluginDAO;
	}
	
	@Override
	protected Object call() throws Exception {
		
		this.updateProgress(0, 1);
		this.updateMessage("Deleting plugin " + plugin.getName() + " ...");
		
		File pluginFile = new File(plugin.getPath());
		if(pluginFile.delete()) {
			pluginDAO.delete(plugin);
		}
		
		
		this.updateProgress(1, 1);
		this.updateMessage("Plugin successfully deleted");
				
		return null;
	}

}
