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
 
package com.owlplug.explore.model;

import com.owlplug.core.model.PluginFormat;
import java.util.List;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class PackageBundle {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  private String downloadUrl;

  private String downloadSha256;
  private PluginFormat format;
  private String technicalUid;
  private String version;
  private long fileSize;
  @ElementCollection(fetch = FetchType.EAGER)
  private List<String> targets;

  @ManyToOne
  private RemotePackage remotePackage;

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

  public String getTechnicalUid() {
    return technicalUid;
  }

  public void setTechnicalUid(String technicalUid) {
    this.technicalUid = technicalUid;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

  public List<String> getTargets() {
    return targets;
  }

  public void setTargets(List<String> targets) {
    this.targets = targets;
  }

  public RemotePackage getRemotePackage() {
    return remotePackage;
  }

  public void setRemotePackage(RemotePackage remotePackage) {
    this.remotePackage = remotePackage;
  }

  public PluginFormat getFormat() {
    return format;
  }

  public void setFormat(PluginFormat format) {
    this.format = format;
  }
  

}
