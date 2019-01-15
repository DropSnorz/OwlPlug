package com.owlplug.core.components;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.owlplug.auth.services.AuthenticationService;
import com.owlplug.core.dao.PluginDAO;
import com.owlplug.core.dao.PluginRepositoryDAO;
import com.owlplug.core.model.GoogleDriveRepository;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginRepository;
import com.owlplug.core.services.PluginRepositoryService;
import com.owlplug.core.tasks.PluginRemoveTask;
import com.owlplug.core.tasks.PluginSyncTask;
import com.owlplug.core.tasks.RepositoryRemoveTask;
import com.owlplug.core.tasks.RepositoryTask;
import com.owlplug.core.tasks.TaskExecutionContext;
import com.owlplug.core.tasks.plugins.discovery.PluginSyncTaskParameters;
import com.owlplug.core.tasks.repositories.IRepositoryStrategy;
import com.owlplug.core.tasks.repositories.RepositoryStrategyParameters;
import com.owlplug.core.tasks.repositories.RepositoryStrategyParameters.RepositoryAction;
import com.owlplug.core.tasks.repositories.RepositoryStrategyResolver;
import com.owlplug.core.utils.SimpleEventListener;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreTaskFactory extends BaseTaskFactory {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ApplicationDefaults applicationDefaults;
  @Autowired
  private Preferences prefs;
  @Autowired
  private PluginDAO pluginDAO;
  @Autowired
  private PluginRepositoryDAO pluginRepositoryDAO;
  @Autowired 
  private PluginRepositoryService repositoryService;
  @Autowired 
  private AuthenticationService authenticationService;
  @Autowired
  private RepositoryStrategyResolver repositoryStrategyResolver;


  private ArrayList<SimpleEventListener> syncPluginsListeners = new ArrayList<>();

  /**
   * Creates a {@link PluginSyncTask} and binds listeners to the success callback.
   * 
   * @return
   */
  public TaskExecutionContext createPluginSyncTask() {

    PluginSyncTaskParameters parameters = new PluginSyncTaskParameters();
    parameters.setPlatform(applicationDefaults.getRuntimePlatform().getOperatingSystem());
    parameters.setPluginDirectory(prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, ""));
    parameters.setFindVST2(prefs.getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false));
    parameters.setFindVST3(prefs.getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false));

    PluginSyncTask task = new PluginSyncTask(parameters, pluginDAO);
    task.setOnSucceeded(e -> {
      notifyListeners(syncPluginsListeners);
    });
    return create(task);
  }
  
  /**
   * Creates a {@link PluginRemoveTask}.
   * @param plugin - plugin to remove
   * @return task execution context
   */
  public TaskExecutionContext createPluginRemoveTask(Plugin plugin) {
    PluginRemoveTask task = new PluginRemoveTask(plugin, pluginDAO);
    
    return create(task);
  }
  
  /**
   * Creates a {@link RepositoryRemoveTask}.
   * @param repository - repository to remove
   * @return task execution context
   */
  public TaskExecutionContext createRepositoryRemoveTask(PluginRepository repository) {
    String localPath = repositoryService.getLocalRepositoryPath(repository);
    RepositoryRemoveTask task = new RepositoryRemoveTask(pluginRepositoryDAO, repository, localPath);
    
    return create(task);
  }
  
  /**
   * Creates a pull {@link RepositoryTask}.
   * @param repository - repository to pull
   * @return task execution  context
   */
  public TaskExecutionContext createRepositoryPullTask(PluginRepository repository) {
    RepositoryStrategyParameters parameters = new RepositoryStrategyParameters();
    parameters.setRepositoryAction(RepositoryAction.PULL);

    parameters.put("target-dir", repositoryService.getLocalRepositoryPath(repository));
    parameters.put("task-name", "Pull repository - " + repository.getName());

    if (repository instanceof GoogleDriveRepository) {
      if (((GoogleDriveRepository) repository).getUserAccount() != null) {
        GoogleCredential credential = authenticationService
            .getGoogleCredential(((GoogleDriveRepository) repository).getUserAccount().getKey());
        parameters.putObject("google-credential", credential);

      }
    }

    IRepositoryStrategy strategy = repositoryStrategyResolver.getStrategy(repository, parameters);
    return create(new RepositoryTask(strategy, parameters, repository));

  }



  public void addSyncPluginsListener(SimpleEventListener eventListener) {
    syncPluginsListeners.add(eventListener);
  }

  public void removeSyncPluginsListener(SimpleEventListener eventListener) {
    syncPluginsListeners.remove(eventListener);
  }

}
