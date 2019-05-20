package com.owlplug.core.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Symlink implements IDirectory {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;
  protected String name;
  protected String path;
  protected String targetPath;
  protected boolean enabled;
  
  @Transient
  protected List<Plugin> pluginList;
  
  public Symlink() {
    
  }
  
  public Symlink(String path, String name, boolean enabled) {
    this.path = path;
    this.name = name;
    this.enabled = enabled;
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

  public String getPath() {
    return path;
  }
  
  public void setPath(String path) {
    this.path = path;
  }
  
  public String getTargetPath() {
    return targetPath;
  }

  public void setTargetPath(String targetPath) {
    this.targetPath = targetPath;
  }

  public boolean isEnabled() {
    return enabled;
  }
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public List<Plugin> getPluginList() {
    return pluginList;
  }

  public void setPluginList(List<Plugin> pluginList) {
    this.pluginList = pluginList;
  }

  @Override
  public String getDisplayName() {
    return name;
  }

  @Override
  public void setDisplayName(String name) {
    // Custom display name are not needed for Symlinks
    
  }
  
}
