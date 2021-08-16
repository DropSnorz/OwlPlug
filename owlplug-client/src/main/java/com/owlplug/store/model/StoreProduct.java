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
 
package com.owlplug.store.model;

import com.owlplug.core.model.PluginStage;
import com.owlplug.core.model.PluginType;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "IDX_PRODUCT_ID", columnList = "id"),
    @Index(name = "IDX_PRODUCT_NAME", columnList = "name") })
public class StoreProduct {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;
  private String pageUrl;
  @Deprecated
  private String downloadUrl;
  private String screenshotUrl;
  private String donateUrl;
  private String creator;
  private String license;
  @Deprecated
  // Version must be be hold by bundles
  private String version;
  @Column(columnDefinition = "text")
  private String description;
  private PluginType type;
  private PluginStage stage;

  @ManyToOne
  private Store store;

  @OneToMany(mappedBy = "product", orphanRemoval = true, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
  private Set<ProductBundle> bundles = new HashSet<>();
  @OneToMany(mappedBy = "product", orphanRemoval = true, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
      CascadeType.REMOVE })
  private Set<ProductTag> tags = new HashSet<>();

  public StoreProduct() {
    super();
  }

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

  @Deprecated
  public String getVersion() {
    return version;
  }

  @Deprecated
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

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public Set<ProductBundle> getBundles() {
    return bundles;
  }

  public void setBundles(Set<ProductBundle> bundles) {
    this.bundles = bundles;
  }

  public Set<ProductTag> getTags() {
    return tags;
  }

  public void setTags(Set<ProductTag> tags) {
    this.tags = tags;
  }

}
