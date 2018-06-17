package com.dropsnorz.owlplug.core.services;

import java.io.File;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.auth.model.UserAccount;
import com.dropsnorz.owlplug.auth.services.AuthentificationService;
import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.dropsnorz.owlplug.core.components.TaskRunner;
import com.dropsnorz.owlplug.core.dao.FileSystemRepositoryDAO;
import com.dropsnorz.owlplug.core.dao.GoogleDriveRepositoryDAO;
import com.dropsnorz.owlplug.core.dao.PluginRepositoryDAO;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyParameters;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyParameters.RepositoryAction;
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
	protected GoogleDriveRepositoryDAO googleDriveRepositoryDAO;
	@Autowired
	protected AuthentificationService authentificationService;
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


			if(((GoogleDriveRepository) repository).getUserAccount() != null) {
				GoogleCredential credential = authentificationService.getGoogleCredential(((GoogleDriveRepository) repository).getUserAccount().getKey());
				parameters.putObject("google-credential", credential);

				taskFactory.createRepositoryTask(repository, parameters).run();
			}


		}
		else {
			taskFactory.createRepositoryTask(repository, parameters).run();

		}

	}

	public void delete(PluginRepository repository) {

		String localPath = getLocalRepositoryPath(repository);
		taskFactory.createRepositoryRemoveTask(repository, localPath).run();
	}

	public void removeAccountReferences(UserAccount account) {

		Iterable<GoogleDriveRepository> repositories = googleDriveRepositoryDAO.findAll();

		for(GoogleDriveRepository repository : repositories) {

			if(repository.getUserAccount() != null 
					&& repository.getUserAccount().getId().equals(account.getId())) {
				repository.setUserAccount(null);
				googleDriveRepositoryDAO.save(repository);
			}
		}
	}

	public String getLocalRepositoryDirectory() {
		String path = prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, null);
		if(path == null) return null;
		return FileUtils.convertPath(path + File.separator + ApplicationDefaults.REPOSITORY_FOLDER_NAME);
	}

	public String getLocalRepositoryPath(PluginRepository repository) {

		String path = getLocalRepositoryDirectory();
		if(path == null) return null;

		return FileUtils.convertPath(path  + File.separator + repository.getName());

	}

}
