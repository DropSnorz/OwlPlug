package com.dropsnorz.owlplug.core.engine.tasks;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.dropsnorz.owlplug.core.model.PluginDirectory;

public class DirectoryRemoveTask extends AbstractTask {

	protected PluginDirectory pluginDirectory;

	public DirectoryRemoveTask(PluginDirectory pluginDirectory){

		this.pluginDirectory = pluginDirectory;
		setName("Remove directory");
	}

	@Override
	protected TaskResult call() throws Exception {

		this.updateProgress(0, 1);
		this.updateMessage("Deleting directory " + pluginDirectory.getName() + " ...");


		File directoryFile = new File(pluginDirectory.getPath());
		
		FileUtils.deleteDirectory(directoryFile);

		this.updateProgress(1, 1);
		this.updateMessage("Directory successfully deleted");

		return null;
	}
}
