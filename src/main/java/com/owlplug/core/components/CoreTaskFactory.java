package com.owlplug.core.components;

import com.owlplug.core.dao.PluginDAO;
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


  public void addSyncPluginsListener(SimpleEventListener eventListener) {
    syncPluginsListeners.add(eventListener);
  }

  public void removeSyncPluginsListener(SimpleEventListener eventListener) {
    syncPluginsListeners.remove(eventListener);
  }

}
