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
import com.owlplug.plugin.model.PluginComponent;
import com.owlplug.plugin.repositories.PluginComponentRepository;
import com.owlplug.project.model.DawPlugin;
import com.owlplug.project.model.DawPluginLookup;
import com.owlplug.project.model.LookupResult;
import com.owlplug.project.repositories.DawPluginRepository;
import com.owlplug.project.repositories.PluginLookupRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PluginLookupService extends BaseService {

  @Autowired
  private PluginComponentRepository pluginComponentRepository;
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

    Specification<PluginComponent> baseSpec = PluginComponentRepository.nameContains(projectPlugin.getName())
            .and(PluginComponentRepository.hasFormat(projectPlugin.getFormat()));

    // Prefer an active (successfully scanned, platform-compatible) component.
    // Fall back to inactive/ghost components only if no active match exists.
    List<PluginComponent> components = pluginComponentRepository.findAll(
            baseSpec.and(PluginComponentRepository.isActive(true)));

    if (components.isEmpty()) {
      components = pluginComponentRepository.findAll(baseSpec);
    }

    if (!components.isEmpty()) {
      lookup.setComponent(components.get(0));
      lookup.setResult(LookupResult.FOUND);
    }

    return lookup;

  }

  public void deleteAllLookups() {
    pluginLookupRepository.deleteAll();
  }

}
