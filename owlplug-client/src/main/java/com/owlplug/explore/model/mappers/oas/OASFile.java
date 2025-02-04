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

package com.owlplug.explore.model.mappers.oas;

import java.util.List;

public class OASFile {

  private List<System> systems;
  private List<String> architectures;
  private List<String> contains;
  private String format;
  private String type;
  private int size;
  private String sha256;
  private String url;

  public List<System> getSystems() {
    return systems;
  }

  public void setSystems(List<System> systems) {
    this.systems = systems;
  }

  public List<String> getArchitectures() {
    return architectures;
  }

  public void setArchitectures(List<String> architectures) {
    this.architectures = architectures;
  }

  public List<String> getContains() {
    return contains;
  }

  public void setContains(List<String> contains) {
    this.contains = contains;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getSha256() {
    return sha256;
  }

  public void setSha256(String sha256) {
    this.sha256 = sha256;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public class System {
    private String type;
    // TODO, number in spec, should be changed ?
    private String min;
    // TODO, number in spec, should be changed ?
    private String max;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getMin() {
      return min;
    }

    public void setMin(String min) {
      this.min = min;
    }

    public String getMax() {
      return max;
    }

    public void setMax(String max) {
      this.max = max;
    }
  }
}
