package com.dropsnorz.owlplug.core.services;

import java.io.File;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.dao.FileSystemRepositoryDAO;
import com.dropsnorz.owlplug.core.dao.PluginRepositoryBaseDAO;
import com.dropsnorz.owlplug.core.engine.repositories.IRepositoryStrategy;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyParameters;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyResolver;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyParameters.RepositoryAction;
import com.dropsnorz.owlplug.core.engine.tasks.RepositoryRemoveTask;
import com.dropsnorz.owlplug.core.engine.tasks.RepositoryTask;
import com.dropsnorz.owlplug.core.model.FileSystemRepository;
import com.dropsnorz.owlplug.core.model.PluginRepository;
import com.dropsnorz.owlplug.core.utils.FileUtils;

@Service
public class PluginRepositoryService {

	@Autowired
	protected Preferences prefs;
	@Autowired
	protected FileSystemRepositoryDAO fileSystemRepositoryDAO;
	@Autowired
	protected RepositoryStrategyResolver repositoryStrategyResolver;
	@Autowired
	protected TaskManager taskManager;
	@Autowired
	protected TaskFactory taskFactory;

	public boolean createRepository(PluginRepository repository){

		if(fileSystemRepositoryDAO.findByName(repository.getName()) == null) {

			fileSystemRepositoryDAO.save((FileSystemRepository)repository);
			return true;

		}

		return false;

	}
	
	public void save(PluginRepository repository) {
		 fileSystemRepositoryDAO.save((FileSystemRepository)repository);
	}


	public void pull(PluginRepository repository) {
		RepositoryStrategyParameters parameters = new RepositoryStrategyParameters();
		parameters.setRepositoryAction(RepositoryAction.PULL);
		parameters.put("target-dir", getLocalRepositoryPath(repository));

		IRepositoryStrategy strategy = repositoryStrategyResolver.getStrategy(repository, parameters);


		taskManager.addTask(new RepositoryTask() {

			@Override
			protected Void call() throws Exception {

				this.updateProgress(0, 1);
				strategy.execute(repository, parameters);
				this.updateProgress(1, 1);

				return null;
			}
		});
	}
	
	public void delete(PluginRepository repository) {
		
		String localPath = getLocalRepositoryPath(repository);
		RepositoryRemoveTask task = taskFactory.createRepositoryRemoveTask(repository, localPath);
		
		taskManager.addTask(task);
		
	}

	public String getLocalRepositoryPath(PluginRepository repository) {

		String path =prefs.get("PLUGIN_DIRECOTRY", null);
		if(path == null) return null;
		return FileUtils.convertPath(path + File.separator + ApplicationDefaults.REPOSITORY_FOLDER_NAME + File.separator + repository.getName());

	}

}
