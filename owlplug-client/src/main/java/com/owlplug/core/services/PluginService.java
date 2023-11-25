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
 
package com.owlplug.core.services;

import com.google.common.collect.Iterables;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.dao.PluginDAO;
import com.owlplug.core.dao.PluginFootprintDAO;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginFootprint;
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.PluginState;
import com.owlplug.core.utils.PluginUtils;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.services.ExploreService;
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PluginService extends BaseService {

  @Autowired
  protected OwlPlugCentralService owlplugCentralService;
  @Autowired
  protected ExploreService exploreService;
  @Autowired
  protected PluginDAO pluginDAO;
  @Autowired
  protected PluginFootprintDAO pluginFootprintDAO;
  @Autowired
  protected CoreTaskFactory taskFactory;
  
  private final Logger log = LoggerFactory.getLogger(this.getClass());


  public void syncPlugins() {
    taskFactory.createPluginSyncTask().schedule();
  }

  public void syncFiles() {
    taskFactory.createFileStatSyncTask().schedule();
  }

  public Iterable<Plugin> getAllPlugins() {
    return pluginDAO.findAll();
  }

  /**
   * Returns an url to retrieve plugin screenshots. Url can be retrieved from
   * registered packages in remote sources or using OwlPlug Central screenshot API.
   * 
   * @param plugin the plugin
   * @return screenshot url
   */
  public String resolveImageUrl(Plugin plugin) {

    String absoluteName = PluginUtils.absoluteName(plugin.getName());
    Iterable<RemotePackage> packages = exploreService.getPackagesByName(absoluteName);

    if (!Iterables.isEmpty(packages)) {
      return Iterables.get(packages, 0).getScreenshotUrl();
    }
    
    if (plugin.getDescriptiveName() != null) {
      packages = exploreService.getPackagesByName(plugin.getDescriptiveName());
      if (!Iterables.isEmpty(packages)) {
        return Iterables.get(packages, 0).getScreenshotUrl();
      }
    }
    
    return owlplugCentralService.getPluginImageUrl(absoluteName);
    
  }
  
  public void disablePlugin(Plugin plugin) {
    
    File originFile = new File(plugin.getPath());
    File destFile = new File(plugin.getPath() + ".disabled");

    if (originFile.renameTo(destFile)) {
      plugin.setDisabled(true);
      plugin.setPath(plugin.getPath() + ".disabled");
      pluginDAO.save(plugin);

    } else {
      log.error("Plugin can't be disabled: failed to rename file {}", plugin.getPath());
    }
    
  }
  
  public void enablePlugin(Plugin plugin) {

    File originFile = new File(plugin.getPath());

    String newPath = plugin.getPath();
    if (plugin.getPath().endsWith(".disabled")) {
      newPath = plugin.getPath().substring(0, plugin.getPath().length() - ".disabled".length());
    }
    File destFile = new File(newPath);

    if (originFile.renameTo(destFile)) {
      plugin.setDisabled(false);
      plugin.setPath(newPath);
      pluginDAO.save(plugin);
    } else {
      log.error("Plugin can't be enabled: failed to rename file {}", plugin.getPath());
    }

  }

  public PluginState getPluginState(Plugin plugin) {

    if (!plugin.isSyncComplete()) {
      return PluginState.UNSTABLE;
    }
    if (plugin.isDisabled()) {
      return PluginState.DISABLED;
    }
    if (plugin.isNativeCompatible()) {
      return PluginState.ACTIVE;
    }
    return PluginState.INSTALLED;
  }

  public Iterable<Plugin> find(String name, PluginFormat pluginFormat) {
    Specification<Plugin> spec = PluginDAO.nameContains(name)
            .and(PluginDAO.hasFormat(pluginFormat));


    return pluginDAO.findAll(spec);
  }

  public Iterable<Plugin> findByComponentName(String name, PluginFormat pluginFormat) {
    Specification<Plugin> spec = PluginDAO.hasComponentName(name)
            .and(PluginDAO.hasFormat(pluginFormat));

    return pluginDAO.findAll(spec);

  }
  
  /**
   * Removes a plugin reference from database.
   * @param plugin - plugin to remove
   */
  public void delete(Plugin plugin) {
    pluginDAO.delete(plugin);
  }
  
  public Plugin save(Plugin plugin) {
    return pluginDAO.save(plugin);
  }
  
  public PluginFootprint save(PluginFootprint pluginFootprint) {
    return pluginFootprintDAO.save(pluginFootprint);
  }
  
  public List<Plugin> getSyncIncompletePlugins() {
    return pluginDAO.findBySyncComplete(false);
  }
}
