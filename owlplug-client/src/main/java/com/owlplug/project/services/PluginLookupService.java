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

    Iterable<Plugin> plugins = pluginService.find(projectPlugin.getName(), projectPlugin.getFormat());

    PluginLookup lookup = new PluginLookup();
    lookup.setProjectPlugin(projectPlugin);
    lookup.setResult(LookupResult.FAILED);

    if (plugins.iterator().hasNext()) {
      lookup.setPlugin(plugins.iterator().next());
      lookup.setResult(LookupResult.MATCH);
    }

    return lookup;

  }

}
