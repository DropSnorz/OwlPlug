/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.Table;

@Entity
@Inheritance
@Table(indexes = { @Index(name = "IDX_PLUGIN_ID", columnList = "id"),
    @Index(name = "IDX_PLUGIN_NAME", columnList = "name") })
public class Plugin {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  protected String name;
  protected String descriptiveName;
  protected String uid;
  protected String category;
  protected String manufacturerName;
  protected String identifier;
  protected String path;
  protected String bundleId;
  protected String version;
  protected String screenshotUrl;
  protected boolean nativeCompatible = false;
  
  protected PluginFormat format;
  protected PluginType type;

  public Plugin() {

  }

 public Plugin(String name, String path, PluginFormat format) {
    this.name = name;
    this.path = path;
    this.format = format;
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

  public String getScreenshotUrl() {
    return screenshotUrl;
  }

  public void setScreenshotUrl(String screenshotUrl) {
    this.screenshotUrl = screenshotUrl;
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

  public PluginType getType() {
    return type;
  }

  public void setType(PluginType type) {
    this.type = type;
  }

  public PluginFormat getFormat() {
    return format;
  }

  public void setFormat(PluginFormat format) {
    this.format = format;
  }

  public boolean isNativeCompatible() {
    return nativeCompatible;
  }

  public void setNativeCompatible(boolean nativeCompatible) {
    this.nativeCompatible = nativeCompatible;
  }

  @Override
  public String toString() {
    return name;
  }

}
