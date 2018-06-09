package com.dropsnorz.owlplug.core.services;

import java.io.File;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.auth.services.AuthentificationService;
import com.dropsnorz.owlplug.core.dao.FileSystemRepositoryDAO;
import com.dropsnorz.owlplug.core.dao.PluginRepositoryDAO;
import com.dropsnorz.owlplug.core.engine.repositories.IRepositoryStrategy;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyParameters;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyParameters.RepositoryAction;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyResolver;
import com.dropsnorz.owlplug.core.engine.tasks.RepositoryRemoveTask;
import com.dropsnorz.owlplug.core.engine.tasks.RepositoryTask;
import com.dropsnorz.owlplug.core.model.GoogleDriveRepository;
import com.dropsnorz.owlplug.core.model.PluginRepository;
import com.dropsnorz.owlplug.core.utils.FileUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

@Service
public class PluginRepositoryService {

	@Autowired
	protected Preferences prefs;
	@Autowired
	protected FileSystemRepositoryDAO fileSystemRepositoryDAO;
	@Autowired
	protected PluginRepositoryDAO pluginRepositoryDAO;
	@Autowired
	protected RepositoryStrategyResolver repositoryStrategyResolver;
	@Autowired
	protected AuthentificationService authentificationService;
	@Autowired
	protected TaskManager taskManager;
	@Autowired
	protected TaskFactory taskFactory;
	@Autowired
	protected ApplicationDefaults applicationDefaults;

	public boolean createRepository(PluginRepository repository){
		
		if(pluginRepositoryDAO.findByName(repository.getName()) == null) {

			pluginRepositoryDAO.save(repository);
			return true;

		}

		return false;

	}
	
	public void save(PluginRepository repository) {
		 pluginRepositoryDAO.save(repository);
	}


	public void pull(PluginRepository repository) {
		RepositoryStrategyParameters parameters = new RepositoryStrategyParameters();
		parameters.setRepositoryAction(RepositoryAction.PULL);
		
		parameters.put("target-dir", getLocalRepositoryPath(repository));
		
		if(repository instanceof GoogleDriveRepository) {
			GoogleCredential credential = authentificationService.getGoogleCredential(((GoogleDriveRepository) repository).getUserAccount().getKey());
			parameters.putObject("google-credential", credential);

		}

		IRepositoryStrategy strategy = repositoryStrategyResolver.getStrategy(repository, parameters);


		taskManager.addTask(new RepositoryTask() {

			@Override
			protected Void call() throws Exception {

				this.updateProgress(0, 1);
				try {
					strategy.execute(repository, parameters);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

		String path =prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, null);
		if(path == null) return null;
		return FileUtils.convertPath(path + File.separator + ApplicationDefaults.REPOSITORY_FOLDER_NAME + File.separator + repository.getName());

	}

}
