package com.owlplug.core.components;

import com.owlplug.core.dao.PluginDAO;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.services.NativeHostService;
import com.owlplug.core.tasks.PluginRemoveTask;
import com.owlplug.core.tasks.PluginSyncTask;
import com.owlplug.core.tasks.TaskExecutionContext;
import com.owlplug.core.tasks.plugins.discovery.PluginSyncTaskParameters;
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
  private NativeHostService nativeHostService;


  private ArrayList<SimpleEventListener> syncPluginsListeners = new ArrayList<>();

  /**
   * Creates a {@link PluginSyncTask} and binds listeners to the success callback.
   * 
   * @return
   */
  public TaskExecutionContext createPluginSyncTask() {

    PluginSyncTaskParameters parameters = new PluginSyncTaskParameters();
    parameters.setPlatform(applicationDefaults.getRuntimePlatform());
    parameters.setVstDirectory(prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, ""));
    parameters.setVst3Directory(prefs.get(ApplicationDefaults.VST3_DIRECTORY_KEY, ""));
    parameters.setFindVst2(prefs.getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false));
    parameters.setFindVst3(prefs.getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false));

    PluginSyncTask task = new PluginSyncTask(parameters, pluginDAO, nativeHostService);
    task.setOnSucceeded(e -> {
      notifyListeners(syncPluginsListeners);
    });
    return create(task);
  }
  
  /**
   * Creates a {@link PluginSyncTask} and binds listeners to the success callback.
   * The task synchronizes plugins in the given directory scope.
   * @param directoryScope
   * @return
   */
  public TaskExecutionContext createPluginSyncTask(String directoryScope) {

    PluginSyncTaskParameters parameters = new PluginSyncTaskParameters();
    parameters.setPlatform(applicationDefaults.getRuntimePlatform());
    parameters.setVstDirectory(prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, ""));
    parameters.setVst3Directory(prefs.get(ApplicationDefaults.VST3_DIRECTORY_KEY, ""));
    parameters.setFindVst2(prefs.getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false));
    parameters.setFindVst3(prefs.getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false));

    PluginSyncTask task = new PluginSyncTask(directoryScope, parameters, pluginDAO, nativeHostService);
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

  public void addSyncPluginsListener(SimpleEventListener eventListener) {
    syncPluginsListeners.add(eventListener);
  }

  public void removeSyncPluginsListener(SimpleEventListener eventListener) {
    syncPluginsListeners.remove(eventListener);
  }

}
