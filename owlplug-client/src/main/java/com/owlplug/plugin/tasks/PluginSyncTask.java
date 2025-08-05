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
 
package com.owlplug.plugin.tasks;

import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskException;
import com.owlplug.core.tasks.TaskResult;
import com.owlplug.host.NativePlugin;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginComponent;
import com.owlplug.plugin.model.PluginFootprint;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.model.PluginType;
import com.owlplug.plugin.model.Symlink;
import com.owlplug.plugin.repositories.PluginFootprintRepository;
import com.owlplug.plugin.repositories.PluginRepository;
import com.owlplug.plugin.repositories.SymlinkRepository;
import com.owlplug.plugin.services.NativeHostService;
import com.owlplug.plugin.tasks.discovery.PluginFileCollector;
import com.owlplug.plugin.tasks.discovery.PluginSyncTaskParameters;
import com.owlplug.plugin.tasks.discovery.SymlinkCollector;
import com.owlplug.plugin.tasks.discovery.fileformats.PluginFile;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OwlPlug task to collect plugin metadata from directories
 * By default, the task collects and sync all plugins from user folders. A directory scope
 * can be defined to reduce the amount of scanned files.
 *
 */
public class PluginSyncTask extends AbstractTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private PluginRepository pluginRepository;
  private SymlinkRepository symlinkRepository;
  private PluginFootprintRepository pluginFootprintRepository;
  private NativeHostService nativeHostService;
  private PluginSyncTaskParameters parameters;


  /**
   * Creates a new PluginSyncTask.
   * @param parameters Task Parameters
   * @param pluginRepository pluginRepository
   * @param pluginFootprintRepository pluginFootprintRepository
   * @param symlinkRepository symlinkRepository
   * @param nativeHostService nativeHostService
   */
  public PluginSyncTask(PluginSyncTaskParameters parameters, 
      PluginRepository pluginRepository,
      PluginFootprintRepository pluginFootprintRepository,
      SymlinkRepository symlinkRepository,
      NativeHostService nativeHostService) {
    this.parameters = parameters;
    this.pluginRepository = pluginRepository;
    this.pluginFootprintRepository = pluginFootprintRepository;
    this.symlinkRepository = symlinkRepository;

    this.nativeHostService = nativeHostService;

    setName("Sync Plugins");
    setMaxProgress(100);

  }

  @Override
  protected TaskResult start() throws Exception {

    log.info("Plugin Sync task started");
    this.updateMessage("Collecting plugins...");
    this.commitProgress(10);

    try {
      Set<PluginFile> collectedPluginFiles = new LinkedHashSet<>();
      PluginFileCollector pluginCollector = new PluginFileCollector(parameters.getPlatform());
      ArrayList<Symlink> collectedSymlinks = new ArrayList<>();
      SymlinkCollector symlinkCollector = new SymlinkCollector(true);
      
      if (parameters.getDirectoryScope() != null) {
        // Delete previous plugins scanned in the directory scope
        pluginRepository.deleteByPathContainingIgnoreCase(parameters.getDirectoryScope());
        symlinkRepository.deleteByPathContainingIgnoreCase(parameters.getDirectoryScope());
      } else {
        // Delete all previous plugins by default (in case of a complete Sync task)
        pluginRepository.deleteAll();
        symlinkRepository.deleteAll();
      }

      // Flushing context to the database as next queries will recreate entities
      pluginRepository.flush();
      symlinkRepository.flush();

      if (parameters.getDirectoryScope() != null) {
        // Plugins are retrieved from a scoped directory
        if (parameters.isFindLv2()) {
          collectedPluginFiles.addAll(pluginCollector.collect(parameters.getDirectoryScope(), PluginFormat.LV2));
        }
        if (parameters.isFindVst3()) {
          collectedPluginFiles.addAll(pluginCollector.collect(parameters.getDirectoryScope(), PluginFormat.VST3));
        }
        if (parameters.isFindVst2()) {
          collectedPluginFiles.addAll(pluginCollector.collect(parameters.getDirectoryScope(), PluginFormat.VST2));
        }
        if (parameters.isFindAu()) {
          collectedPluginFiles.addAll(pluginCollector.collect(parameters.getDirectoryScope(), PluginFormat.AU));
        }

        collectedSymlinks.addAll(symlinkCollector.collect(parameters.getDirectoryScope()));

      } else {
        // Plugins are retrieved from regulars directories
        String vst2Directory = parameters.getVst2Directory();
        String vst3Directory = parameters.getVst3Directory();
        String auDirectory = parameters.getAuDirectory();
        String lv2Directory = parameters.getLv2Directory();

        if (parameters.isFindLv2()) {
          collectedPluginFiles.addAll(pluginCollector.collect(lv2Directory, PluginFormat.LV2));
          collectedSymlinks.addAll(symlinkCollector.collect(lv2Directory));
          for (String path : parameters.getLv2ExtraDirectories()) {
            collectedPluginFiles.addAll(pluginCollector.collect(path, PluginFormat.LV2));
            collectedSymlinks.addAll(symlinkCollector.collect(path));
          }
        }

        if (parameters.isFindVst3()) {
          collectedPluginFiles.addAll(pluginCollector.collect(vst3Directory, PluginFormat.VST3));
          collectedSymlinks.addAll(symlinkCollector.collect(vst3Directory));
          for (String path : parameters.getVst3ExtraDirectories()) {
            collectedPluginFiles.addAll(pluginCollector.collect(path, PluginFormat.VST3));
            collectedSymlinks.addAll(symlinkCollector.collect(path));
          }
        }

        if (parameters.isFindVst2()) {
          collectedPluginFiles.addAll(pluginCollector.collect(vst2Directory, PluginFormat.VST2));
          collectedSymlinks.addAll(symlinkCollector.collect(vst2Directory));
          for (String path : parameters.getVst2ExtraDirectories()) {
            collectedPluginFiles.addAll(pluginCollector.collect(path, PluginFormat.VST2));
            collectedSymlinks.addAll(symlinkCollector.collect(path));
          }
        }

        if (parameters.isFindAu()) {
          collectedPluginFiles.addAll(pluginCollector.collect(auDirectory, PluginFormat.AU));
          collectedSymlinks.addAll(symlinkCollector.collect(auDirectory));
          for (String path : parameters.getAuExtraDirectories()) {
            collectedPluginFiles.addAll(pluginCollector.collect(path, PluginFormat.AU));
            collectedSymlinks.addAll(symlinkCollector.collect(path));
          }
        }
      }
      
      log.info(collectedPluginFiles.size() + " plugins collected");
      
      //Save all discovered symlinks
      symlinkRepository.saveAll(collectedSymlinks);

      for (PluginFile pluginFile : collectedPluginFiles) {
        Plugin plugin = pluginFile.toPlugin();
        
        PluginFootprint pluginFootprint = pluginFootprintRepository.findByPath(plugin.getPath());
        
        if (pluginFootprint == null) {
          pluginFootprint = new PluginFootprint(plugin.getPath());
          pluginFootprintRepository.saveAndFlush(pluginFootprint);
        }
        plugin.setFootprint(pluginFootprint);
        pluginRepository.save(plugin);

        if (nativeHostService.isNativeHostEnabled() && nativeHostService.getCurrentPluginLoader().isAvailable()
            && pluginFootprint.isNativeDiscoveryEnabled() && !plugin.isDisabled()) {

          log.debug("Load plugin using native discovery: " + plugin.getPath());
          this.updateMessage("Exploring plugin " + plugin.getName());
          List<NativePlugin> nativePlugins = nativeHostService.loadPlugin(plugin.getPath());

          if (nativePlugins != null && !nativePlugins.isEmpty()) {
            log.debug("Found {} components (nativePlugin) for plugin {}", nativePlugins.size(), plugin.getName());

            plugin.setNativeCompatible(true);

            for (NativePlugin nativePlugin : nativePlugins) {
              PluginComponent component = createComponentFromNative(nativePlugin);
              component.setPlugin(plugin);
              plugin.getComponents().add(component);
              log.debug("Created component {} for plugin {}", component.getName(), plugin.getName());
            }

            // Hardcode plugin properties from the first component (nativePlugin) retrieved.
            mapPluginPropertiesFromNative(plugin, nativePlugins.get(0));

          }
        }
        
        plugin.setSyncComplete(true);
        pluginRepository.save(plugin);

        this.commitProgress(80.0 / collectedPluginFiles.size());
      }


      this.updateProgress(1, 1);
      this.updateMessage("Plugins synchronized");
      log.info("Plugin Sync task complete");
      
      return success();

    } catch (Exception e) {
      this.updateMessage("Plugins synchronization failed: " + e.getMessage());
      log.error("Plugins synchronization failed", e);
      throw new TaskException("Plugins synchronization failed", e);

    }

  }

  private PluginComponent createComponentFromNative(NativePlugin nativePlugin) {
    PluginComponent pluginComponent = new PluginComponent();
    pluginComponent.setName(nativePlugin.getName());
    pluginComponent.setDescriptiveName(nativePlugin.getDescriptiveName());
    pluginComponent.setVersion(nativePlugin.getVersion());
    pluginComponent.setCategory(nativePlugin.getCategory());
    pluginComponent.setManufacturerName(nativePlugin.getManufacturerName());
    pluginComponent.setIdentifier(nativePlugin.getFileOrIdentifier());
    pluginComponent.setUid(String.valueOf(nativePlugin.getUid()));

    if (nativePlugin.isInstrument()) {
      pluginComponent.setType(PluginType.INSTRUMENT);
    } else {
      pluginComponent.setType(PluginType.EFFECT);
    }

    return pluginComponent;
  }

  private void mapPluginPropertiesFromNative(Plugin plugin, NativePlugin nativePlugin) {
    plugin.setDescriptiveName(nativePlugin.getDescriptiveName());
    plugin.setVersion(nativePlugin.getVersion());
    plugin.setCategory(nativePlugin.getCategory());
    plugin.setManufacturerName(nativePlugin.getManufacturerName());
    plugin.setIdentifier(nativePlugin.getFileOrIdentifier());
    plugin.setUid(String.valueOf(nativePlugin.getUid()));

    if (nativePlugin.isInstrument()) {
      plugin.setType(PluginType.INSTRUMENT);
    } else {
      plugin.setType(PluginType.EFFECT);
    }

  }
}
