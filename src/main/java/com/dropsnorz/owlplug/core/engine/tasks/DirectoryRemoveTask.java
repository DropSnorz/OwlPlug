package com.dropsnorz.owlplug.core.engine.tasks;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.dropsnorz.owlplug.core.model.PluginDirectory;

import javafx.concurrent.Task;

public class DirectoryRemoveTask extends Task {

	protected PluginDirectory pluginDirectory;

	public DirectoryRemoveTask(PluginDirectory pluginDirectory){

		this.pluginDirectory = pluginDirectory;
	}

	@Override
	protected Object call() throws Exception {

		this.updateProgress(0, 1);
		this.updateMessage("Deleting directory " + pluginDirectory.getName() + " ...");


		File directoryFile = new File(pluginDirectory.getPath());
		
		FileUtils.deleteDirectory(directoryFile);
		directoryFile.delete();

		this.updateProgress(1, 1);
		this.updateMessage("Directory successfully deleted");

		return null;
	}


}
