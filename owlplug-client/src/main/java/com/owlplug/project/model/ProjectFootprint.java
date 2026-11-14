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

import com.owlplug.core.model.Label;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.BatchSize;

/**
 * User data attached to a project path. Unlike {@link DawProject}, footprints
 * are not deleted on project sync, so their content survives re-synchronizations.
 */
@Entity
@BatchSize(size = 100)
@Table(indexes = { @Index(name = "IDX_PROJECT_FOOTPRINT_PATH", columnList = "path") })
public class ProjectFootprint {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Column(length = 512)
  private String path;

  @Column(length = 2000)
  private String note;

  @ManyToMany(fetch = FetchType.EAGER)
  @BatchSize(size = 100)
  private Set<Label> labels = new HashSet<>();

  public ProjectFootprint() {
  }

  public ProjectFootprint(String path) {
    this.path = path;
  }

  public Long getId() {
    return id;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public Set<Label> getLabels() {
    return labels;
  }

  public void setLabels(Set<Label> labels) {
    this.labels = labels;
  }

}
