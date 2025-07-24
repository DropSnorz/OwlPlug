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

package com.owlplug.plugin.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "IDX_PLUGIN_FOOTPRINT_ID", columnList = "id"),
    @Index(name = "IDX_PLUGIN_FOOTPRINT_PATH", columnList = "path") })
public class PluginFootprint {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;
  protected String path;
  protected boolean nativeDiscoveryEnabled = true;

  protected String screenshotUrl;
  
  public PluginFootprint() {
  }
  
  public PluginFootprint(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public boolean isNativeDiscoveryEnabled() {
    return nativeDiscoveryEnabled;
  }

  public void setNativeDiscoveryEnabled(boolean nativeDiscoveryEnabled) {
    this.nativeDiscoveryEnabled = nativeDiscoveryEnabled;
  }

  public String getScreenshotUrl() {
    return screenshotUrl;
  }

  public void setScreenshotUrl(String screenshotUrl) {
    this.screenshotUrl = screenshotUrl;
  }

  public Long getId() {
    return id;
  }

}
