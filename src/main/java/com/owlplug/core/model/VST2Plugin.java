package com.owlplug.core.model;

import javax.persistence.Entity;

@Entity
public class VST2Plugin extends Plugin {

  public VST2Plugin() {
    super();
  }

  public VST2Plugin(String name, String path) {
    super(name, path);
  }

  @Override
  public PluginFormat getFormat() {
    return PluginFormat.VST2;
  }

}