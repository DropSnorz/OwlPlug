package com.dropsnorz.owlplug.engine.tasks;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.services.PluginService;

import javafx.concurrent.Task;

public class PluginRemoveTask extends Task {

	protected Plugin plugin;
	protected PluginService pluginService;
	
	public PluginRemoveTask(Plugin plugin, PluginService pluginService){
		
		this.plugin = plugin;
		this.pluginService = pluginService;
	}
	
	@Override
	protected Object call() throws Exception {
		
		this.updateProgress(0, 1);
		this.updateMessage("Deleting plugin " + plugin.getName() + " ...");
		pluginService.removePlugin(this.plugin);
		this.updateProgress(1, 1);
				
		return null;
	}

}
