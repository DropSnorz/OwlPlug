package com.dropsnorz.owlplug.core.engine.tasks;

import java.util.List;

import com.dropsnorz.owlplug.core.dao.PluginDAO;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.services.PluginService;


public class PluginSyncTask extends AbstractTask{

	protected PluginService pluginService;
	protected PluginDAO pluginDAO;

	public PluginSyncTask(PluginService pluginService, PluginDAO pluginDAO) {
		this.pluginService = pluginService;
		this.pluginDAO = pluginDAO;
	}


	@Override
	protected TaskResult call() throws Exception {

		this.updateMessage("Syncing Plugins...");
		this.updateProgress(0, 2);

		try {
			List<Plugin> plugins = pluginService.explore();
			this.updateProgress(1, 2);
			
			pluginDAO.deleteAll();
			pluginDAO.saveAll(plugins);

			this.updateProgress(2, 2);
			this.updateMessage("Plugins synchronized");
			
			return success();

		}catch (Exception e) {
			this.updateMessage("Plugins synchronization failed. Check your plugin directory.");
			throw new TaskException("Plugins synchronization failed");

		}

	}



}
