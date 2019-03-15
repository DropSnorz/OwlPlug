package com.owlplug.core.model.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteVersion {
  public String version;
  
  public RemoteVersion() {
    
  }
}
