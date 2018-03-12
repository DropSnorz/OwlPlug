package com.dropsnorz.owlplug.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.controllers.PluginsController;
import com.dropsnorz.owlplug.dao.PluginDAO;
import com.dropsnorz.owlplug.engine.tasks.PluginRemoveTask;
import com.dropsnorz.owlplug.engine.tasks.SyncPluginTask;
import com.dropsnorz.owlplug.model.Plugin;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

@Service
public class TaskFactory {
	
	@Autowired
	PluginExplorer pluginExplorer;
	@Autowired
	PluginService pluginService;
	@Autowired
	TaskManager taskManager;
	@Autowired
	PluginDAO pluginRepository;
	
	
	@Autowired
	PluginsController pluginsController;
	
	public void run(Task task) {
		taskManager.addTask(task);
	}
	
	public SyncPluginTask createSyncPluginTask() {
		
		SyncPluginTask task = new SyncPluginTask(pluginExplorer, pluginRepository);
		
		task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {
				
				pluginsController.refreshPlugins();
				
			}
		});
		return task;
	}
	
    public PluginRemoveTask createpluginRemoveTask(Plugin plugin) {
		
    	PluginRemoveTask task = new PluginRemoveTask(plugin, pluginService);
		
		task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {
				
				pluginsController.refreshPlugins();
				
			}
		});
		
		return task;
	}

}
