package com.dropsnorz.owlplug.engine.tasks;

import java.util.List;

import com.dropsnorz.owlplug.dao.PluginDAO;
import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.services.PluginExplorer;

import javafx.concurrent.Task;

public class SyncPluginTask extends Task<Void>{
	
	protected PluginExplorer pluginExplorer;
	protected PluginDAO pluginDAO;
	
	public SyncPluginTask(PluginExplorer pluginExplorer, PluginDAO pluginDAO) {
		this.pluginExplorer = pluginExplorer;
		this.pluginDAO = pluginDAO;
	}


	@Override
	protected Void call() throws Exception {
		
		this.updateMessage("Syncing Plugins...");
		this.updateProgress(0, 2);
		
		List<Plugin> plugins = pluginExplorer.explore();
		
		this.updateProgress(1, 2);

		pluginDAO.deleteAll();
		pluginDAO.save(plugins);
		
		this.updateProgress(2, 2);

		return null;
	}
	
	

}
