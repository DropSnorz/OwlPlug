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

package com.owlplug.project.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String path;
  private String name;
  private DawApplication application;
  private String appFullName;
  private String formatVersion;
  @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, orphanRemoval = true, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
  private Set<ProjectPlugin> plugins = new HashSet<>();
  private Date lastModified;
  private Date createdAt;

  public Long getId() {
    return id;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public DawApplication getApplication() {
    return application;
  }

  public void setApplication(DawApplication application) {
    this.application = application;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAppFullName() {
    return appFullName;
  }

  public void setAppFullName(String appName) {
    this.appFullName = appName;
  }

  public Set<ProjectPlugin> getPlugins() {
    return plugins;
  }

  public void setPlugins(Set<ProjectPlugin> plugins) {
    this.plugins = plugins;
  }

  public String getFormatVersion() {
    return formatVersion;
  }

  public void setFormatVersion(String formatVersion) {
    this.formatVersion = formatVersion;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public List<ProjectPlugin> getPluginByLookupResult(LookupResult result) {
    return plugins.stream()
            .filter(p -> p.getLookup() != null && p.getLookup().getResult().equals(result))
                    .collect(Collectors.toList());
  }
}
