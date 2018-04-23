package com.dropsnorz.owlplug.services;

import java.io.File;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.dao.FileSystemRepositoryDAO;
import com.dropsnorz.owlplug.dao.PluginRepositoryBaseDAO;
import com.dropsnorz.owlplug.engine.repositories.IRepositoryStrategy;
import com.dropsnorz.owlplug.engine.repositories.RepositoryStrategyParameters;
import com.dropsnorz.owlplug.engine.repositories.RepositoryStrategyParameters.RepositoryAction;
import com.dropsnorz.owlplug.engine.repositories.RepositoryStrategyResolver;
import com.dropsnorz.owlplug.engine.tasks.RepositoryTask;
import com.dropsnorz.owlplug.model.FileSystemRepository;
import com.dropsnorz.owlplug.model.PluginRepository;
import com.dropsnorz.owlplug.utils.FileUtils;

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

	public boolean createRepository(PluginRepository repository){

		if(fileSystemRepositoryDAO.findByName(repository.getName()) == null) {

			fileSystemRepositoryDAO.save((FileSystemRepository)repository);
			return true;

		}

		return false;

	}


	public void pull(PluginRepository repository) {
		RepositoryStrategyParameters parameters = new RepositoryStrategyParameters();
		parameters.setRepositoryAction(RepositoryAction.PULL);
		parameters.put("target-dir", getLocalRepositoryPath(repository));

		IRepositoryStrategy strategy = repositoryStrategyResolver.getStrategy(repository, parameters);


		taskManager.addTask(new RepositoryTask() {

			@Override
			protected Void call() throws Exception {

				strategy.execute(repository, parameters);
				this.updateProgress(1, 1);

				return null;
			}
		});
	}

	public String getLocalRepositoryPath(PluginRepository repository) {

		String path =prefs.get("VST2_DIRECTORY", null);
		if(path == null) return null;
		return FileUtils.convertPath(path + File.separator + ApplicationDefaults.REPOSITORY_FOLDER_NAME + File.separator + repository.getName());

	}

}
