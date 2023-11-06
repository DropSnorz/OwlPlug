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

import com.owlplug.core.model.Plugin;
import com.owlplug.core.services.BaseService;
import com.owlplug.core.services.PluginService;
import com.owlplug.project.dao.PluginLookupDAO;
import com.owlplug.project.dao.ProjectPluginDAO;
import com.owlplug.project.model.LookupResult;
import com.owlplug.project.model.PluginLookup;
import com.owlplug.project.model.ProjectPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PluginLookupService extends BaseService {

  @Autowired
  private PluginService pluginService;
  @Autowired
  private PluginLookupDAO pluginLookupDAO;
  @Autowired
  private ProjectPluginDAO projectPluginDAO;

  public void createLookup(ProjectPlugin projectPlugin) {

    projectPlugin.setLookup(lookup(projectPlugin));
    projectPluginDAO.save(projectPlugin);

  }

  public PluginLookup lookup(ProjectPlugin projectPlugin) {

    PluginLookup lookup = new PluginLookup();
    lookup.setProjectPlugin(projectPlugin);
    lookup.setResult(LookupResult.FAILED);

    Iterable<Plugin> plugins = pluginService.find(projectPlugin.getName(), projectPlugin.getFormat());

    if (plugins.iterator().hasNext()) {
      lookup.setPlugin(plugins.iterator().next());
      lookup.setResult(LookupResult.MATCH);
    } else {
      // Resolve the Plugin file (based on known subcomponents)
      plugins = pluginService.findByComponentName(projectPlugin.getName(), projectPlugin.getFormat());

      if (plugins.iterator().hasNext()) {
        lookup.setPlugin(plugins.iterator().next());
        lookup.setResult(LookupResult.MATCH);
      }

    }

    return lookup;

  }

}
