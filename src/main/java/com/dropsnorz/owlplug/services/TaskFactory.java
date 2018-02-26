package com.dropsnorz.owlplug.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.engine.tasks.SyncPluginTask;

@Service
public class TaskFactory {
	
	@Autowired
	PluginExplorer pluginExplorer;
	
	public SyncPluginTask createSyncPluginTask() {
		
		return new SyncPluginTask(pluginExplorer);
	}

}
