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

public interface IPlugin {

  /**
   * Retrieve the associated Plugin object from this Plugin object.
   * In case of Component, it returns the parent plugin.
   * In case of Plugin, it should return itself.
   * @return plugin
   */
  Plugin asPlugin();

  /**
   * Get object identifier.
   * The identifier is not guaranteed to be unique across Plugin and Component.
   * @return id
   */
  Long getId();

  String getName();

  String getDescriptiveName();

  String getVersion();

  String getUid();

  String getCategory();

  String getManufacturerName();

  String getIdentifier();

  String getBundleId();

  PluginType getType();

}
