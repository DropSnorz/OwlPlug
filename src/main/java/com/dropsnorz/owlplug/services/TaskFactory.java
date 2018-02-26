package com.dropsnorz.owlplug.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.engine.tasks.PluginRemoveTask;
import com.dropsnorz.owlplug.engine.tasks.SyncPluginTask;
import com.dropsnorz.owlplug.model.Plugin;

import javafx.concurrent.Task;

@Service
public class TaskFactory {
	
	@Autowired
	PluginExplorer pluginExplorer;
	@Autowired
	PluginService pluginService;
	@Autowired
	TaskManager taskManager;
	
	public void run(Task task) {
		taskManager.addTask(task);
	}
	
	public SyncPluginTask createSyncPluginTask() {
		
		return new SyncPluginTask(pluginExplorer);
	}
	
    public PluginRemoveTask createpluginRemoveTask(Plugin plugin) {
		
		return new PluginRemoveTask(plugin, pluginService);
	}

}
