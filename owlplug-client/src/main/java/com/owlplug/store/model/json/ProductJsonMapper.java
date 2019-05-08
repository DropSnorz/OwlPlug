package com.owlplug.store.model.json;

import java.util.List;

public class ProductJsonMapper {

  private String name;
  private String pageUrl;
  @Deprecated
  private String downloadUrl;
  private String screenshotUrl;
  private String donateUrl;
  private String creator;
  private String version;
  private String description;
  private String type;
  private List<BundleJsonMapper> bundles;
  private String stage;
  private List<String> tags;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPageUrl() {
    return pageUrl;
  }

  public void setPageUrl(String pageUrl) {
    this.pageUrl = pageUrl;
  }

  @Deprecated
  public String getDownloadUrl() {
    return downloadUrl;
  }

  @Deprecated
  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public String getScreenshotUrl() {
    return screenshotUrl;
  }

  public void setScreenshotUrl(String screenshotUrl) {
    this.screenshotUrl = screenshotUrl;
  }

  public String getDonateUrl() {
    return donateUrl;
  }

  public void setDonateUrl(String donateURL) {
    this.donateUrl = donateURL;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setDescription(String version) {
    this.description = version;
  }

  public List<BundleJsonMapper> getBundles() {
    return bundles;
  }

  public void setBundles(List<BundleJsonMapper> bundles) {
    this.bundles = bundles;
  }

  public String getStage() {
    return stage;
  }

  public void setStage(String stage) {
    this.stage = stage;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

}
