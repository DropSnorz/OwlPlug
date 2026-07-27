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

import com.owlplug.plugin.model.PluginFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.util.Objects;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class DawPlugin {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String uid;
  private String name;

  /**
   * Plugin file locator as recorded by the host DAW project.
   * Depending on the DAW, this may be a bare file name (e.g. Reaper stores
   * only "reacomp.dll") or a full absolute path (e.g. Ableton stores
   * "C:\Program Files\VSTPlugins\Serum.dll"). It is not guaranteed to exist
   * or to be usable to locate the plugin on the current machine.
   */
  private String path;

  @Enumerated(EnumType.STRING)
  private PluginFormat format;

  @ManyToOne
  private DawProject project;

  @OneToOne(mappedBy = "dawPlugin", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private DawPluginLookup lookup;

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

  public DawProject getProject() {
    return project;
  }

  public void setProject(DawProject project) {
    this.project = project;
  }

  public DawPluginLookup getLookup() {
    return lookup;
  }

  public void setLookup(DawPluginLookup lookup) {
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
    DawPlugin that = (DawPlugin) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name)
            && Objects.equals(path, that.path)
            && format == that.format && Objects.equals(project, that.project);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, path, format, project);
  }
}


