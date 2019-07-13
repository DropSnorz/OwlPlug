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
 
package com.owlplug.core.services;

import com.google.common.collect.Iterables;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.dao.PluginDAO;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.utils.PluginUtils;
import com.owlplug.store.model.StoreProduct;
import com.owlplug.store.services.StoreService;
import java.util.prefs.Preferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PluginService {

  @Autowired
  protected OwlPlugCentralService owlplugCentralService;
  @Autowired
  protected StoreService pluginStoreService;
  @Autowired
  protected ApplicationDefaults applicationDefaults;
  @Autowired
  protected Preferences prefs;
  @Autowired
  protected PluginDAO pluginDAO;
  @Autowired
  protected CoreTaskFactory taskFactory;

  public void syncPlugins() {
    taskFactory.createPluginSyncTask().schedule();
  }

  /**
   * Returns an url to retrieve plugin screenshots. Url can be retrieve from
   * registered products in store or using OwlPlug Central screenshot API.
   * 
   * @param plugin the plugin
   * @return screenshot url
   */
  public String resolveImageUrl(Plugin plugin) {

    String absoluteName = PluginUtils.absoluteName(plugin.getName());
    Iterable<StoreProduct> products = pluginStoreService.getProductsByName(absoluteName);

    if (!Iterables.isEmpty(products)) {
      return Iterables.get(products, 0).getScreenshotUrl();
    }
    
    if (plugin.getDescriptiveName() != null) {
      products = pluginStoreService.getProductsByName(plugin.getDescriptiveName());
      if (!Iterables.isEmpty(products)) {
        return Iterables.get(products, 0).getScreenshotUrl();
      }
    }
    
    return owlplugCentralService.getPluginImageUrl(absoluteName);
    
  }
  
  /**
   * Removes a plugin reference from database.
   * @param plugin - plugin to remoeve
   */
  public void delete(Plugin plugin) {
    pluginDAO.delete(plugin);
  }
  
  public Plugin save(Plugin plugin) {
    return pluginDAO.save(plugin);
  }
}
