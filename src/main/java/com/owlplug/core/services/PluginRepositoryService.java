package com.owlplug.core.services;

import com.owlplug.auth.model.UserAccount;
import com.owlplug.auth.services.AuthenticationService;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.dao.FileSystemRepositoryDAO;
import com.owlplug.core.dao.GoogleDriveRepositoryDAO;
import com.owlplug.core.dao.PluginRepositoryDAO;
import com.owlplug.core.model.GoogleDriveRepository;
import com.owlplug.core.model.PluginRepository;
import com.owlplug.core.utils.FileUtils;
import java.io.File;
import java.util.prefs.Preferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
  protected AuthenticationService authentificationService;
  @Autowired
  protected CoreTaskFactory taskFactory;
  @Autowired
  protected ApplicationDefaults applicationDefaults;

  /**
   * Creates and store a new repository instance.
   * 
   * @param repository - the new repository
   * @return true if repository has been created, false otherwise
   */
  public boolean createRepository(PluginRepository repository) {

    if (pluginRepositoryDAO.findByName(repository.getName()) == null) {
      pluginRepositoryDAO.save(repository);
      return true;
    }
    return false;

  }
  
  public PluginRepository findByName(String name) {
    return pluginRepositoryDAO.findByName(name);
  }
  
  public Iterable<PluginRepository> findAll() {
    return pluginRepositoryDAO.findAll();
  }

  public void save(PluginRepository repository) {
    pluginRepositoryDAO.save(repository);
  }


  /**
   * Clears all userAccount fields matching the given UserAccount. This is usefull
   * where an account is deleted but associated repositories are still managed by
   * owlplug.
   * 
   * @param account - UserAccount to clear
   */
  public void removeAccountReferences(UserAccount account) {

    Iterable<GoogleDriveRepository> repositories = googleDriveRepositoryDAO.findAll();

    for (GoogleDriveRepository repository : repositories) {
      if (repository.getUserAccount() != null && repository.getUserAccount().getId().equals(account.getId())) {
        repository.setUserAccount(null);
        googleDriveRepositoryDAO.save(repository);
      }
    }
  }

  public String getLocalRepositoryDirectory() {
    String path = prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, null);
    if (path == null) {
      return null;
    }
    return FileUtils.convertPath(path + File.separator + ApplicationDefaults.REPOSITORY_FOLDER_NAME);
  }

  public String getLocalRepositoryPath(PluginRepository repository) {

    String path = getLocalRepositoryDirectory();
    if (path == null) {
      return null;
    }
    return FileUtils.convertPath(path + File.separator + repository.getName());

  }

}
