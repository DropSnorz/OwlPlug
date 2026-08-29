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

import com.owlplug.plugin.model.PluginFormat;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.List;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;

@Entity
public class PackageBundle {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Deprecated
  private String name;
  private String downloadUrl;

  private String downloadSha256;
  private String technicalUid;
  private String version;
  private long fileSize;
  // Position of this bundle's source file in its parent OAS plugin version's files array.
  // Used to re-fetch this file's download details from the OAS registry detail endpoint
  // when they are not provided by the bulk registry sync. Defaults to 0, which is harmless
  // for OwlPlug-registry bundles that never read it.
  // Column explicitly named to avoid "order", a reserved SQL keyword that ddl-auto: update's
  // incremental ALTER TABLE path does not reliably quote (unlike full CREATE TABLE DDL).
  // ColumnDefault backfills existing rows (NOT NULL int column) when added via ALTER TABLE.
  @Column(name = "bundle_order")
  @ColumnDefault("0")
  private int order;
  @ElementCollection(fetch = FetchType.EAGER)
  @BatchSize(size = 100)
  private List<String> targets;

  @ElementCollection(fetch = FetchType.EAGER)
  @BatchSize(size = 100)
  private List<String> formats;

  @ManyToOne
  private RemotePackage remotePackage;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Deprecated
  public String getName() {
    return name;
  }

  @Deprecated
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

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
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

  public List<String> getFormats() {
    return formats;
  }

  public void setFormats(List<String> formats) {
    this.formats = formats;
  }
}
