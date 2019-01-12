package com.dropsnorz.owlplug.core.components;

import com.dropsnorz.owlplug.core.dao.PluginDAO;
import com.dropsnorz.owlplug.core.tasks.AbstractTask;
import com.dropsnorz.owlplug.core.tasks.PluginSyncTask;
import com.dropsnorz.owlplug.core.tasks.TaskExecutionContext;
import com.dropsnorz.owlplug.core.tasks.plugins.discovery.PluginSyncTaskParameters;
import com.dropsnorz.owlplug.core.utils.SimpleEventListener;
import com.dropsnorz.owlplug.store.dao.StoreDAO;
import com.dropsnorz.owlplug.store.dao.StoreProductDAO;
import com.dropsnorz.owlplug.store.tasks.StoreSyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskFactory {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ApplicationDefaults applicationDefaults;
  @Autowired
  private Preferences prefs;
  @Autowired
  private TaskRunner taskManager;
  @Autowired
  private PluginDAO pluginDAO;
  @Autowired
  private StoreDAO pluginStoreDAO;
  @Autowired
  private StoreProductDAO storeProductDAO;

  private ArrayList<SimpleEventListener> syncPluginsListeners = new ArrayList<>();
  private ArrayList<SimpleEventListener> syncStoresListeners = new ArrayList<>();

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
   * Creates a {@link StoreSyncTask} and binds listeners to the success callback.
   * 
   * @return
   */
  public TaskExecutionContext createStoreSyncTask() {

    StoreSyncTask task = new StoreSyncTask(pluginStoreDAO, storeProductDAO);
    task.setOnSucceeded(e -> {
      notifyListeners(syncStoresListeners);
    });
    return create(task);
  }

  public TaskExecutionContext create(AbstractTask task) {
    return buildContext(task);
  }

  private void notifyListeners(List<SimpleEventListener> listeners) {
    for (SimpleEventListener listener : listeners) {
      listener.onAction();
    }
  }

  private TaskExecutionContext buildContext(AbstractTask task) {
    return new TaskExecutionContext(task, taskManager);
  }

  public void addSyncPluginsListener(SimpleEventListener eventListener) {
    syncPluginsListeners.add(eventListener);
  }

  public void removeSyncPluginsListener(SimpleEventListener eventListener) {
    syncPluginsListeners.remove(eventListener);
  }

  public void addSyncStoresListener(SimpleEventListener eventListener) {
    syncStoresListeners.add(eventListener);
  }

  public void removeSyncStoresListener(SimpleEventListener eventListener) {
    syncStoresListeners.remove(eventListener);
  }

}
