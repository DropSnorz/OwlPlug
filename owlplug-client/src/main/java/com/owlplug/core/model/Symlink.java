package com.owlplug.core.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Symlink {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;
  protected String path;
  protected boolean enabled;
  
  public Symlink() {
    
  }
  
  public Symlink(String path, boolean enabled) {
    this.path = path;
    this.enabled = enabled;
  }
  
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getPath() {
    return path;
  }
  public void setPath(String path) {
    this.path = path;
  }
  public boolean isEnabled() {
    return enabled;
  }
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  
  
  

}
