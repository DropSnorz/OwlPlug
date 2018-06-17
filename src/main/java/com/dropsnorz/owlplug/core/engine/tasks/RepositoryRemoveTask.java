package com.dropsnorz.owlplug.core.engine.tasks;

import com.dropsnorz.owlplug.core.dao.PluginRepositoryDAO;
import com.dropsnorz.owlplug.core.model.PluginDirectory;
import com.dropsnorz.owlplug.core.model.PluginRepository;

import javafx.concurrent.Task;

public class RepositoryRemoveTask  extends AbstractTask {

	protected PluginRepositoryDAO repositoryDAO;
	protected PluginRepository repository;
	protected String localPath;
	
	public RepositoryRemoveTask(PluginRepositoryDAO repositoryDAO, PluginRepository repository, String localPath) {
		
		this.repositoryDAO = repositoryDAO;
		this.repository = repository;
		this.localPath = localPath;
	}
	
	@Override
	protected TaskResult call() throws Exception{
		
		this.updateProgress(0, 2);
		this.updateMessage("Deleting repository " + repository.getName() + " ...");
		
		if(repositoryDAO.existsById(repository.getId())) {
			repositoryDAO.delete(repository);
			
			this.updateProgress(1, 2);
			this.updateMessage("Deleting files ...");
			
			PluginDirectory directory = new PluginDirectory(localPath);
			
			DirectoryRemoveTask directoryRemoveTask = new DirectoryRemoveTask(directory);
			directoryRemoveTask.call();
			
			this.updateProgress(2, 2);
			this.updateMessage("Repository successfully deleted");
		}
		
		
		return null;
	}

}
