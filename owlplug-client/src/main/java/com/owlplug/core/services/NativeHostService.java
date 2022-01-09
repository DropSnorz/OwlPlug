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
 
package com.owlplug.core.services;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.host.NativePlugin;
import com.owlplug.host.loaders.NativePluginLoader;
import com.owlplug.host.loaders.jni.JNINativePluginLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class NativeHostService extends BaseService {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private List<NativePluginLoader> pluginLoaders = new ArrayList<>();
  private NativePluginLoader currentPluginLoader = null;

  @PostConstruct
  private void init() {
    pluginLoaders.add(JNINativePluginLoader.getInstance());
    for (NativePluginLoader loader : pluginLoaders) {
      loader.init();
      if (currentPluginLoader == null && loader.isAvailable()) {
        currentPluginLoader = loader;
      }
    }
  }

  public NativePluginLoader getCurrentPluginLoader() {
    return currentPluginLoader;
  }

  public NativePlugin loadPlugin(String path) {
    if (currentPluginLoader != null) {
      return currentPluginLoader.loadPlugin(path);
    } else {
      log.error("Native plugin loader not set");
      throw new IllegalStateException("Native plugin loader not set");
    }
  }
  
  public boolean isNativeHostEnabled() {
    return this.getPreferences().getBoolean(ApplicationDefaults.NATIVE_HOST_ENABLED_KEY, false);
  }
  
  public boolean isNativeHostAvailable() {
    for (NativePluginLoader loader : pluginLoaders) {
      if (loader.isAvailable()) {
        return true;
      }
    }
    return false;
  }

}
