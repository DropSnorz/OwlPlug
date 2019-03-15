package com.owlplug.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is no longer used. 
 * In OwlPlug 0.6.0 Repositories were remote sources of plugin. The remote
 * souces can be pulled inside plugin directories.
 * This feature has been deactivated since OwlPlug 0.7.0
 * @deprecated
 *
 */
public abstract class PluginRepository implements IDirectory {


  protected Long id;
  protected String name;
  protected List<Plugin> pluginList;
  protected String displayName;

  PluginRepository(String name) {
    this();
    this.name = name;
  }

  PluginRepository() {
    pluginList = new ArrayList<Plugin>();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Plugin> getPluginList() {
    return pluginList;
  }

  public void setPluginList(List<Plugin> pluginList) {
    this.pluginList = pluginList;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String toString() {
    if (displayName != null)
      return displayName;
    return name;
  }

}
