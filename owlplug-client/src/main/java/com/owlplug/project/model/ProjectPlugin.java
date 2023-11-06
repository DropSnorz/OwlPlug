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

import com.owlplug.core.model.PluginFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.util.Objects;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class ProjectPlugin {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String uid;
  private String name;

  @Deprecated
  private String fileName;
  private String path;

  private PluginFormat format;

  @ManyToOne
  private Project project;

  @OneToOne(mappedBy = "projectPlugin", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private PluginLookup lookup;

  public Long getId() {
    return id;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Deprecated
  public String getFileName() {
    return fileName;
  }

  @Deprecated
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public PluginFormat getFormat() {
    return format;
  }

  public void setFormat(PluginFormat format) {
    this.format = format;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public PluginLookup getLookup() {
    return lookup;
  }

  public void setLookup(PluginLookup lookup) {
    this.lookup = lookup;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectPlugin that = (ProjectPlugin) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name)
            && Objects.equals(fileName, that.fileName) && Objects.equals(path, that.path)
            && format == that.format && Objects.equals(project, that.project);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, fileName, path, format, project);
  }
}


