package com.dropsnorz.owlplug.engine.tasks;

import com.dropsnorz.owlplug.services.PluginExplorer;

import javafx.concurrent.Task;

public class SyncPluginTask extends Task<Void>{
	
	protected PluginExplorer pluginExplorer;
	
	public SyncPluginTask(PluginExplorer pluginExplorer) {
		this.pluginExplorer = pluginExplorer;
	}


	@Override
	protected Void call() throws Exception {
		
		this.updateMessage("Syncing Plugins...");
		this.updateProgress(0, 3);
		
		pluginExplorer.explore();
		
		this.updateProgress(3,3);

		return null;
	}
	
	

}
