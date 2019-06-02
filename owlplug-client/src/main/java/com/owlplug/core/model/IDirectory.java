package com.owlplug.core.model;

public interface IDirectory {

  public String getName();
  
  public boolean isStale();

  public String getDisplayName();

  public void setDisplayName(String name);

}
