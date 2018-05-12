package com.dropsnorz.owlplug.engine.tasks;

import java.util.List;

import com.dropsnorz.owlplug.dao.PluginDAO;
import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.services.PluginService;

import javafx.concurrent.Task;

public class SyncPluginTask extends Task<Void>{
	
	protected PluginService pluginService;
	protected PluginDAO pluginDAO;
	
	public SyncPluginTask(PluginService pluginService, PluginDAO pluginDAO) {
		this.pluginService = pluginService;
		this.pluginDAO = pluginDAO;
	}


	@Override
	protected Void call() throws Exception {
		
		this.updateMessage("Syncing Plugins...");
		this.updateProgress(0, 2);
		
		List<Plugin> plugins = pluginService.explore();
		
		this.updateProgress(1, 2);

		pluginDAO.deleteAll();
		pluginDAO.saveAll(plugins);
		
		this.updateProgress(2, 2);

		return null;
	}
	
	

}
