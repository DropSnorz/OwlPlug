package com.dropsnorz.owlplug.core.services;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.dropsnorz.owlplug.core.dao.PluginDAO;
import com.dropsnorz.owlplug.core.model.Plugin;
import java.util.prefs.Preferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PluginService {

	@Autowired
	protected ApplicationDefaults applicationDefaults;
	@Autowired
	protected Preferences prefs;
	@Autowired
	protected PluginDAO pluginDAO;
	@Autowired
	protected TaskFactory taskFactory;


	public void syncPlugins() {
		taskFactory.createPluginSyncTask().run();
	}

	public void removePlugin(Plugin plugin) {
		taskFactory.createPluginRemoveTask(plugin).run();
	}


}
