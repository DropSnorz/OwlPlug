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
 
package com.owlplug.explore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class PackageTag {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  @ManyToOne
  private RemotePackage remotePackage;

  public PackageTag() {

  }

  public PackageTag(String name) {
    super();
    this.name = name;
  }

  /**
   * Creates a PackageTag for a given package.
   * 
   * @param name    - name of the tag
   * @param remotePackage - related package
   */
  public PackageTag(String name, RemotePackage remotePackage) {
    super();
    this.name = name;
    this.remotePackage = remotePackage;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RemotePackage getRemotePackage() {
    return remotePackage;
  }

  public void setRemotePackage(RemotePackage remotePackage) {
    this.remotePackage = remotePackage;
  }
}
