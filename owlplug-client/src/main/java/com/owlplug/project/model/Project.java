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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String path;
  private String name;
  private DawApplication application;

  private String appFullName;

  @OneToMany(mappedBy = "project", orphanRemoval = true, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
  private Set<ProjectPlugin> plugins = new HashSet<>();

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
}
