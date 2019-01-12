package com.owlplug.core.model;

import java.util.List;

public class PluginDirectory implements IDirectory {

  protected String name;
  protected String displayName;
  protected String path;
  protected List<Plugin> pluginList;

  public PluginDirectory() {

  }

  public PluginDirectory(String path) {
    this.path = path;
  }

  public PluginDirectory(String name, String path, List<Plugin> pluginList) {
    super();
    this.name = name;
    this.path = path;
    this.pluginList = pluginList;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public List<Plugin> getPluginList() {
    return pluginList;
  }

  public void setPluginList(List<Plugin> pluginList) {
    this.pluginList = pluginList;
  }

  @Override
  public String toString() {
    if (displayName != null) {
      return displayName;
    }
    return name;

  }

}
