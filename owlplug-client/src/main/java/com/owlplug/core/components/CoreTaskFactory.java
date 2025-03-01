/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.core.components;

import com.owlplug.core.dao.FileStatDAO;
import com.owlplug.core.dao.PluginDAO;
import com.owlplug.core.dao.PluginFootprintDAO;
import com.owlplug.core.dao.SymlinkDAO;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.services.NativeHostService;
import com.owlplug.core.services.PluginService;
import com.owlplug.core.tasks.FileSyncTask;
import com.owlplug.core.tasks.PluginRemoveTask;
import com.owlplug.core.tasks.PluginSyncTask;
import com.owlplug.core.tasks.SimpleEventListener;
import com.owlplug.core.tasks.TaskExecutionContext;
import com.owlplug.core.tasks.plugins.discovery.PluginSyncTaskParameters;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.project.components.ProjectTaskFactory;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreTaskFactory extends BaseTaskFactory {

  @Autowired
  private ApplicationDefaults applicationDefaults;
  @Autowired
  private ApplicationPreferences prefs;
  @Autowired
  private PluginDAO pluginDAO;
  @Autowired
  private PluginService pluginService;
  @Autowired
  private PluginFootprintDAO pluginFootprintDAO;
  @Autowired
  private SymlinkDAO symlinkDAO;
  @Autowired
  private NativeHostService nativeHostService;
  @Autowired
  private ProjectTaskFactory projectTaskFactory;

  @Autowired
  private FileStatDAO fileStatDAO;


  private ArrayList<SimpleEventListener> syncPluginsListeners = new ArrayList<>();

  /**
   * Creates a {@link PluginSyncTask} and binds listeners to the success callback.
   * 
   * @return taskExecutionContext
   */
  public TaskExecutionContext createPluginSyncTask() {

    return createPluginSyncTask(null);
  }
  
  /**
   * Creates a {@link PluginSyncTask} and binds listeners to the success callback.
   * The task synchronizes plugins in the given directory scope.
   * @param directoryScope directory scope path
   * @return taskExecutionContext
   */
  public TaskExecutionContext createPluginSyncTask(String directoryScope) {

    PluginSyncTaskParameters parameters = new PluginSyncTaskParameters();
    parameters.setPlatform(applicationDefaults.getRuntimePlatform());
    parameters.setVst2Directory(prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, ""));
    parameters.setVst3Directory(prefs.get(ApplicationDefaults.VST3_DIRECTORY_KEY, ""));
    parameters.setAuDirectory(prefs.get(ApplicationDefaults.AU_DIRECTORY_KEY, ""));
    parameters.setLv2Directory(prefs.get(ApplicationDefaults.LV2_DIRECTORY_KEY, ""));
    parameters.setFindVst2(prefs.getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false));
    parameters.setFindVst3(prefs.getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false));
    parameters.setFindAu(prefs.getBoolean(ApplicationDefaults.AU_DISCOVERY_ENABLED_KEY, false));
    parameters.setFindLv2(prefs.getBoolean(ApplicationDefaults.LV2_DISCOVERY_ENABLED_KEY, false));
    parameters.setVst2ExtraDirectories(prefs.getList(ApplicationDefaults.VST2_EXTRA_DIRECTORY_KEY));
    parameters.setVst3ExtraDirectories(prefs.getList(ApplicationDefaults.VST3_EXTRA_DIRECTORY_KEY));
    parameters.setAuExtraDirectories(prefs.getList(ApplicationDefaults.AU_EXTRA_DIRECTORY_KEY));
    parameters.setLv2ExtraDirectories(prefs.getList(ApplicationDefaults.LV2_EXTRA_DIRECTORY_KEY));

    if (directoryScope != null) {
      parameters.setDirectoryScope(FileUtils.convertPath(directoryScope));
    }
    
    PluginSyncTask syncTask = new PluginSyncTask(parameters,
        pluginDAO, 
        pluginFootprintDAO, 
        symlinkDAO, 
        nativeHostService);
    
    syncTask.setOnSucceeded(syncEvent -> {
      notifyListeners(syncPluginsListeners);
      TaskExecutionContext lookupTask = projectTaskFactory.createLookupTask();
      lookupTask.getTask().setOnScheduled(lookupEvent -> {
        if (directoryScope != null) {
          createFileStatSyncTask(directoryScope).scheduleNow();
        } else {
          createFileStatSyncTask().scheduleNow();
        }
      });
      lookupTask.scheduleNow();
    });

    return create(syncTask);
  }

  public TaskExecutionContext createFileStatSyncTask() {

    Set<String> directorySet = pluginService.getDirectoriesExplorationSet();
    FileSyncTask task = new FileSyncTask(fileStatDAO, directorySet.stream().toList());

    return create(task);
  }

  public TaskExecutionContext createFileStatSyncTask(String directoryScope) {
    FileSyncTask task = new FileSyncTask(fileStatDAO, directoryScope);
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
