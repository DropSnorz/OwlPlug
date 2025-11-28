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

package com.owlplug.project.services;

import com.owlplug.core.services.BaseService;
import com.owlplug.core.utils.PluginUtils;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.services.PluginService;
import com.owlplug.project.model.DawPlugin;
import com.owlplug.project.model.DawPluginLookup;
import com.owlplug.project.model.LookupResult;
import com.owlplug.project.repositories.DawPluginRepository;
import com.owlplug.project.repositories.PluginLookupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PluginLookupService extends BaseService {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private PluginService pluginService;
  @Autowired
  private PluginLookupRepository pluginLookupRepository;
  @Autowired
  private DawPluginRepository dawPluginRepository;

  public void createLookup(DawPlugin projectPlugin) {

    projectPlugin.setLookup(lookup(projectPlugin));
    dawPluginRepository.save(projectPlugin);

  }

  public DawPluginLookup lookup(DawPlugin projectPlugin) {

    DawPluginLookup lookup = new DawPluginLookup();
    lookup.setDawPlugin(projectPlugin);
    lookup.setResult(LookupResult.MISSING);

    String originalName = projectPlugin.getName();
    // Normalize the plugin name (remove platform suffixes like x64, x32, etc.)
    // This helps match plugins where Studio One includes platform info in the name
    // but the installed plugin doesn't (or vice versa)
    String normalizedName = PluginUtils.absoluteName(originalName);

    log.debug("Looking up plugin: '{}' (normalized: '{}') format: {}",
        originalName, normalizedName, projectPlugin.getFormat());

    // Try normalized name first
    Iterable<Plugin> plugins = pluginService.find(normalizedName, projectPlugin.getFormat());
    if (plugins.iterator().hasNext()) {
      Plugin found = plugins.iterator().next();
      lookup.setPlugin(found);
      lookup.setResult(LookupResult.FOUND);
      return lookup;
    }

    // Try component name with normalized name
    plugins = pluginService.findByComponentName(normalizedName, projectPlugin.getFormat());
    if (plugins.iterator().hasNext()) {
      Plugin found = plugins.iterator().next();
      lookup.setPlugin(found);
      lookup.setResult(LookupResult.FOUND);
      return lookup;
    }

    // Fallback: try original name if normalized name didn't match
    if (!normalizedName.equals(originalName)) {
      plugins = pluginService.find(originalName, projectPlugin.getFormat());
      if (plugins.iterator().hasNext()) {
        Plugin found = plugins.iterator().next();
        lookup.setPlugin(found);
        lookup.setResult(LookupResult.FOUND);
        return lookup;
      }

      plugins = pluginService.findByComponentName(originalName, projectPlugin.getFormat());
      if (plugins.iterator().hasNext()) {
        Plugin found = plugins.iterator().next();
        lookup.setPlugin(found);
        lookup.setResult(LookupResult.FOUND);
        return lookup;
      }
    }

    return lookup;

  }

  public void deleteAllLookups() {
    pluginLookupRepository.deleteAll();
  }

}
