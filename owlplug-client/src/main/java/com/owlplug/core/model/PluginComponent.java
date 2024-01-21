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

package com.owlplug.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "IDX_PLUGIN_COMPONENT_ID", columnList = "id") })
public class PluginComponent {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  protected String name;
  protected String descriptiveName;
  protected String uid;
  protected String category;
  protected String manufacturerName;
  protected String identifier;
  protected String bundleId;
  protected String version;
  @Enumerated(EnumType.STRING)
  protected PluginType type;

  @ManyToOne
  @JsonIgnore
  private Plugin plugin;

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

  public String getDescriptiveName() {
    return descriptiveName;
  }

  public void setDescriptiveName(String descriptiveName) {
    this.descriptiveName = descriptiveName;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getManufacturerName() {
    return manufacturerName;
  }

  public void setManufacturerName(String manufacturerName) {
    this.manufacturerName = manufacturerName;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getBundleId() {
    return bundleId;
  }

  public void setBundleId(String bundleId) {
    this.bundleId = bundleId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public PluginType getType() {
    return type;
  }

  public void setType(PluginType type) {
    this.type = type;
  }

  public Plugin getPlugin() {
    return plugin;
  }

  public void setPlugin(Plugin plugin) {
    this.plugin = plugin;
  }

}
