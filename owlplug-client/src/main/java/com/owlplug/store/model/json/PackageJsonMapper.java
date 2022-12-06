package com.owlplug.store.model.json;


import java.util.Map;

public class PackageJsonMapper {

  private String slug;
  private String latestVersion;

  private Map<String, PackageVersionJsonMapper> versions;

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getLatestVersion() {
    return latestVersion;
  }

  public void setLatestVersion(String latestVersion) {
    this.latestVersion = latestVersion;
  }

  public Map<String, PackageVersionJsonMapper> getVersions() {
    return versions;
  }

  public void setVersions(Map<String, PackageVersionJsonMapper> versions) {
    this.versions = versions;
  }
}
