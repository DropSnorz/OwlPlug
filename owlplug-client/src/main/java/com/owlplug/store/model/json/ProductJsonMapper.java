/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.store.model.json;

import java.util.List;

public class ProductJsonMapper {

  private String name;
  private String technicalUid;
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

  public String getTechnicalUid() {
    return technicalUid;
  }

  public void setTechnicalUid(String technicalUid) {
    this.technicalUid = technicalUid;
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
