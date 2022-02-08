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

package com.owlplug.host.loaders.jni;

import com.owlplug.host.NativePlugin;
import com.owlplug.host.loaders.NativePluginLoader;

public class JNINativePluginLoader implements NativePluginLoader {

  private static JNINativePluginLoader INSTANCE;
  JNIPluginMapper nativePluginMapper;

  private JNINativePluginLoader() {

  }

  public static JNINativePluginLoader getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new JNINativePluginLoader();
    }
    return INSTANCE;
  }

  @Override
  public void init() {
    nativePluginMapper = JNIPluginMapper.getInstance();
    nativePluginMapper.init();
  }

  @Override
  public void open() {

  }

  @Override
  public NativePlugin loadPlugin(String path) {
    return nativePluginMapper.mapPlugin(path);
  }

  @Override
  public void close() {

  }

  @Override
  public boolean isAvailable() {
    return nativePluginMapper.isNativeLibraryLoaded();
  }

  @Override
  public String getName() {
    return "OwlPlug JNI (legacy)";
  }

  @Override
  public String getId() {
    return "owlplug-jni";
  }

  @Override
  public String toString() {
    return this.getName();
  }

}
