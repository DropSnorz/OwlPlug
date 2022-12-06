package com.owlplug.store.model.json;

import java.util.Map;

public class RegistryJsonMapper {

  private String name;
  private String url;

  private Map<String, PackageJsonMapper> packages;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Map<String, PackageJsonMapper> getPackages() {
    return packages;
  }

  public void setPackages(Map<String, PackageJsonMapper> packages) {
    this.packages = packages;
  }
}
