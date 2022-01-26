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

package com.owlplug.host.loaders;

import com.owlplug.host.NativePlugin;

public class DummyPluginLoader implements NativePluginLoader {

  private static DummyPluginLoader INSTANCE;

  public static DummyPluginLoader getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DummyPluginLoader();
    }
    return INSTANCE;
  }

  private DummyPluginLoader() {
    // Private constructor for singleton
  }

  @Override
  public void init() {

  }

  @Override
  public void open() {

  }

  @Override
  public NativePlugin loadPlugin(String path) {
    return null;
  }

  @Override
  public void close() {

  }

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public String getName() {
    return "No loader";
  }

  @Override
  public String getId() {
    return "dummy-loader";
  }

  @Override
  public String toString() {
    return this.getName();
  }
}
