/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.explore.model.mappers.registry;

import java.util.List;

public class BundleMapper {

  private String name;
  private List<String> targets;
  private String downloadUrl;

  private String downloadSha256;

  @Deprecated
  private String format;

  private List<String> formats;
  private String version;
  private String technicalUid;
  private long fileSize;

  public String getName() {
    return name;
  }

  public List<String> getFormats() {
    return formats;
  }

  public void setFormats(List<String> formats) {
    this.formats = formats;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getTargets() {
    return targets;
  }

  public void setTargets(List<String> targets) {
    this.targets = targets;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public String getDownloadSha256() {
    return downloadSha256;
  }

  public void setDownloadSha256(String downloadSha256) {
    this.downloadSha256 = downloadSha256;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

  @Deprecated
  public String getFormat() {
    return format;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getTechnicalUid() {
    return technicalUid;
  }

  public void setTechnicalUid(String technicalUid) {
    this.technicalUid = technicalUid;
  }

  @Deprecated
  public void setFormat(String format) {
    this.format = format;
  }

}
