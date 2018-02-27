package com.dropsnorz.owlplug.engine.tasks;

import java.util.List;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.repositories.PluginRepository;
import com.dropsnorz.owlplug.services.PluginExplorer;

import javafx.concurrent.Task;

public class SyncPluginTask extends Task<Void>{
	
	protected PluginExplorer pluginExplorer;
	protected PluginRepository pluginRepository;
	
	public SyncPluginTask(PluginExplorer pluginExplorer, PluginRepository pluginRepository) {
		this.pluginExplorer = pluginExplorer;
		this.pluginRepository = pluginRepository;
	}


	@Override
	protected Void call() throws Exception {
		
		this.updateMessage("Syncing Plugins...");
		this.updateProgress(0, 2);
		
		List<Plugin> plugins = pluginExplorer.explore();
		
		this.updateProgress(1, 2);

		pluginRepository.deleteAll();
		pluginRepository.save(plugins);
		
		this.updateProgress(2, 2);

		return null;
	}
	
	

}
