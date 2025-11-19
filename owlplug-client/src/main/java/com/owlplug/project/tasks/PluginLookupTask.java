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

package com.owlplug.project.tasks;

import com.google.common.collect.Iterables;
import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskResult;
import com.owlplug.project.model.DawPlugin;
import com.owlplug.project.repositories.DawPluginRepository;
import com.owlplug.project.services.PluginLookupService;
import java.text.DecimalFormat;

public class PluginLookupTask extends AbstractTask {

  private PluginLookupService pluginLookupService;
  private DawPluginRepository dawPluginRepository;

  public PluginLookupTask(DawPluginRepository dawPluginRepository, PluginLookupService pluginLookupService) {
    this.dawPluginRepository = dawPluginRepository;
    this.pluginLookupService = pluginLookupService;
    setName("Lookup DAW Plugins");
  }


  @Override
  protected TaskResult start() throws Exception {

    this.updateMessage("Starting project plugins lookup task");
    this.updateProgress(0,1);

    pluginLookupService.deleteAllLookups();
    Iterable<DawPlugin> plugins = dawPluginRepository.findAll();

    this.setMaxProgress(Iterables.size(plugins));
    for (DawPlugin plugin : plugins) {
      pluginLookupService.createLookup(plugin);
      this.commitProgress(1);
      this.updateMessage("Resolving plugin references from projects ("
                             + new DecimalFormat("#").format(getCommittedProgress())
                             + "/"
                             + new DecimalFormat("#").format(getMaxProgress())
                             + ")");
    }

    this.updateMessage("All projects and plugins are up-to-date");
    this.updateProgress(1,1);

    return completed();
  }
}
