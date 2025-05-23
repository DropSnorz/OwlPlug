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

import com.owlplug.core.model.PluginStage;
import com.owlplug.core.model.PluginType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(indexes = { @Index(name = "IDX_PACKAGE_ID", columnList = "id"),
    @Index(name = "IDX_PACKAGE_NAME", columnList = "name") })
public class RemotePackage {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;
  private String slug;
  private String pageUrl;
  @Deprecated
  private String downloadUrl;
  private String screenshotUrl;
  private String donateUrl;
  private String creator;
  private String license;

  private String version;
  @Column(columnDefinition = "text")
  private String description;
  @Enumerated(EnumType.STRING)
  private PluginType type;
  @Enumerated(EnumType.STRING)
  private PluginStage stage;

  @ManyToOne
  private RemoteSource remoteSource;

  @OneToMany(mappedBy = "remotePackage", orphanRemoval = true, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
  private Set<PackageBundle> bundles = new HashSet<>();
  @OneToMany(mappedBy = "remotePackage", orphanRemoval = true, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
      CascadeType.REMOVE })
  private Set<PackageTag> tags = new HashSet<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

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

  public void setDonateUrl(String donateUrl) {
    this.donateUrl = donateUrl;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getLicense() {
    return license;
  }

  public void setLicense(String license) {
    this.license = license;
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

  public void setDescription(String description) {
    this.description = description;
  }

  public PluginType getType() {
    return type;
  }

  public void setType(PluginType type) {
    this.type = type;
  }

  public PluginStage getStage() {
    return stage;
  }

  public void setStage(PluginStage stage) {
    this.stage = stage;
  }

  public RemoteSource getRemoteSource() {
    return remoteSource;
  }

  public void setRemoteSource(RemoteSource remoteSource) {
    this.remoteSource = remoteSource;
  }

  public Set<PackageBundle> getBundles() {
    return bundles;
  }

  public void setBundles(Set<PackageBundle> bundles) {
    this.bundles = bundles;
  }

  public Set<PackageTag> getTags() {
    return tags;
  }

  public void setTags(Set<PackageTag> tags) {
    this.tags = tags;
  }

}
