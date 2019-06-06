/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.core.tasks;

import com.owlplug.core.dao.PluginDAO;
import com.owlplug.core.dao.SymlinkDAO;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.Symlink;
import com.owlplug.core.services.NativeHostService;
import com.owlplug.core.tasks.plugins.discovery.PluginFileCollector;
import com.owlplug.core.tasks.plugins.discovery.PluginSyncTaskParameters;
import com.owlplug.core.tasks.plugins.discovery.SymlinkCollector;
import com.owlplug.core.tasks.plugins.discovery.fileformats.PluginFile;
import com.owlplug.host.NativeHost;
import com.owlplug.host.NativePlugin;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OwlPlug task to collect plugin metadatas from directories
 * By default, the task collects and sync all plugins from user folders. A directory scope
 * can be defined to reduce the amount of scanned files.
 *
 */
public class PluginSyncTask extends AbstractTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private PluginDAO pluginDAO;
  private SymlinkDAO symlinkDAO;
  private PluginSyncTaskParameters parameters;
  private String directoryScope = null;

  private boolean useNativeHost = false;
  private NativeHost nativeHost;


  /**
   * Creates a new PluginSyncTask.
   * @param parameters Task Parameters
   * @param pluginDAO pluginDAO
   * @param symlinkDAO symlinkDAO
   * @param nativeHostService nativeHostService
   */
  public PluginSyncTask(PluginSyncTaskParameters parameters, 
      PluginDAO pluginDAO, SymlinkDAO symlinkDAO, NativeHostService nativeHostService) {
    this.parameters = parameters;
    this.pluginDAO = pluginDAO;
    this.symlinkDAO = symlinkDAO;

    nativeHost = nativeHostService.getNativeHost();
    useNativeHost = nativeHostService.isNativeHostEnabled();

    setName("Sync Plugins");
    setMaxProgress(100);

  }

  /**
   * Creates a new PluginSyncTask scoped to a subdirectory. Only plugins in the directory scope will be scanned.
   * @param directoryScope plugin subdirectory. 
   * @param parameters Task Parameters
   * @param pluginDAO pluginDAO
   * @param symlinkDAO symlinkDAO
   * @param nativeHostService nativeHostService
   */
  public PluginSyncTask(String directoryScope, 
      PluginSyncTaskParameters parameters, 
      PluginDAO pluginDAO,
      SymlinkDAO symlinkDAO,
      NativeHostService nativeHostService) {
    
    this.parameters = parameters;
    this.pluginDAO = pluginDAO;
    this.symlinkDAO = symlinkDAO;
    
    this.directoryScope = directoryScope;

    nativeHost = nativeHostService.getNativeHost();
    useNativeHost = nativeHostService.isNativeHostEnabled();

    setName("Sync Plugins");
    setMaxProgress(100);

  }

  @Override
  protected TaskResult call() throws Exception {

    this.updateMessage("Collecting plugins...");
    this.commitProgress(10);

    try {
      ArrayList<PluginFile> collectedPluginFiles = new ArrayList<>();
      PluginFileCollector pluginCollector = new PluginFileCollector(parameters.getPlatform());
      ArrayList<Symlink> collectedSymlinks = new ArrayList<>();
      SymlinkCollector symlinkCollector = new SymlinkCollector(true);


      if (directoryScope != null) {
        // Plugins are retrieved from a scoped directory
        if (parameters.isFindVst2()) {
          collectedPluginFiles.addAll(pluginCollector.collect(directoryScope, PluginFormat.VST2));
        }
        if (parameters.isFindVst3()) {
          collectedPluginFiles.addAll(pluginCollector.collect(directoryScope, PluginFormat.VST3));
        }
        collectedSymlinks.addAll(symlinkCollector.collect(directoryScope));

      } else {
        // Plugins are retrieved from regulars directories
        String vstDirectory = parameters.getVstDirectory();
        String vst3Directory = parameters.getVst3Directory();

        if (parameters.isFindVst2()) {
          collectedPluginFiles.addAll(pluginCollector.collect(vstDirectory, PluginFormat.VST2));
          collectedSymlinks.addAll(symlinkCollector.collect(vstDirectory));
        }
        if (parameters.isFindVst3()) {
          collectedPluginFiles.addAll(pluginCollector.collect(vst3Directory, PluginFormat.VST3));
          collectedSymlinks.addAll(symlinkCollector.collect(vstDirectory));
        }
      }

      ArrayList<Plugin> discoveredPlugins = new ArrayList<>();
      for (PluginFile pluginFile : collectedPluginFiles) {
        Plugin plugin = pluginFile.toPlugin();
        if (plugin != null) {
          discoveredPlugins.add(plugin);
        }

        if (useNativeHost && nativeHost.isAvailable()) {
          this.updateMessage("Exploring plugin " + plugin.getName());
          NativePlugin nativePlugin = nativeHost.loadPlugin(plugin.getPath());
          if(nativePlugin != null) {
            plugin.setNativeCompatible(true);
            plugin.setDescriptiveName(nativePlugin.getDescriptiveName());
            plugin.setVersion(nativePlugin.getVersion());
            plugin.setCategory(nativePlugin.getCategory());
            plugin.setManufacturerName(nativePlugin.getManufacturerName());
            plugin.setIdentifier(nativePlugin.getFileOrIdentifier());
            plugin.setUid(String.valueOf(nativePlugin.getUid()));
          }
        }

        this.commitProgress(80.0 / collectedPluginFiles.size());
      }

      this.updateMessage("Applying plugin changes");
      
      if(directoryScope != null) {
        // Delete previous plugins scanned in the directory scope
        pluginDAO.deleteByPathContainingIgnoreCase(directoryScope);
        symlinkDAO.deleteByPathContainingIgnoreCase(directoryScope);
      } else {
        // Delete all previous plugins by default (in case of a complete Sync task)
        pluginDAO.deleteAll();
        symlinkDAO.deleteAll();
      }
      pluginDAO.saveAll(discoveredPlugins);
      symlinkDAO.saveAll(collectedSymlinks);
      
      this.updateProgress(1, 1);
      this.updateMessage("Plugins synchronized");

      return success();

    } catch (Exception e) {
      log.error("Plugins synchronization failed", e);
      this.updateMessage("Plugins synchronization failed. Check your plugin directory.");
      throw new TaskException("Plugins synchronization failed");

    }

  }
}
