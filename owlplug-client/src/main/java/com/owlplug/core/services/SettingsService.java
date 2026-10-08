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

import com.owlplug.core.components.ApplicationDefaults.Prefs;
import com.owlplug.core.components.ApplicationPreferences;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.model.OperatingSystem;
import com.owlplug.explore.repositories.RemotePackageRepository;
import com.owlplug.explore.repositories.RemoteSourceRepository;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.repositories.FileStatRepository;
import com.owlplug.plugin.repositories.PluginRepository;
import com.owlplug.project.repositories.DawProjectRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService extends BaseService {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private PluginRepository pluginRepository;
  @Autowired
  private RemoteSourceRepository remoteSourceRepository;
  @Autowired
  private RemotePackageRepository packageRepository;
  @Autowired
  private FileStatRepository fileStatRepository;
  @Autowired
  private DawProjectRepository projectRepository;
  @Autowired
  private ImageCache imageCache;

  @PostConstruct
  private void initialize() {
    
    ApplicationPreferences prefs = this.getPreferences();
    // Init default settings
    if (prefs.get(Prefs.Plugins.VST2_DIRECTORY, null) == null) {
      prefs.put(Prefs.Plugins.VST2_DIRECTORY,
          this.getApplicationDefaults().getDefaultPluginPath(PluginFormat.VST2));
    }
    if (prefs.get(Prefs.Plugins.VST3_DIRECTORY, null) == null) {
      prefs.put(Prefs.Plugins.VST3_DIRECTORY,
          this.getApplicationDefaults().getDefaultPluginPath(PluginFormat.VST3));
    }
    if (prefs.get(Prefs.Plugins.AU_DIRECTORY, null) == null
        && this.getApplicationDefaults().getRuntimePlatform().getOperatingSystem().equals(OperatingSystem.MAC)) {
      prefs.put(Prefs.Plugins.AU_DIRECTORY,
          this.getApplicationDefaults().getDefaultPluginPath(PluginFormat.AU));
    }
    if (prefs.get(Prefs.Plugins.LV2_DIRECTORY, null) == null) {
      prefs.put(Prefs.Plugins.LV2_DIRECTORY,
          this.getApplicationDefaults().getDefaultPluginPath(PluginFormat.LV2));
    }
    // On Linux, the default plugin directories moved to user-level paths (e.g. ~/.vst3) since
    // system-wide ones (e.g. /usr/lib/vst3) require root privileges to write to. Seed those
    // legacy system-wide paths as default additional (scan-only) directories instead, so
    // distro-packaged plugins already installed there remain discoverable.
    if (this.getApplicationDefaults().getRuntimePlatform().getOperatingSystem().equals(OperatingSystem.LINUX)) {
      seedDefaultExtraDirectory(prefs, Prefs.Plugins.VST2_EXTRA_DIRECTORY, PluginFormat.VST2);
      seedDefaultExtraDirectory(prefs, Prefs.Plugins.VST3_EXTRA_DIRECTORY, PluginFormat.VST3);
      seedDefaultExtraDirectory(prefs, Prefs.Plugins.LV2_EXTRA_DIRECTORY, PluginFormat.LV2);
    }
    if (prefs.get(Prefs.Plugins.VST2_DISCOVERY_ENABLED, null) == null) {
      prefs.putBoolean(Prefs.Plugins.VST2_DISCOVERY_ENABLED, Boolean.TRUE);
    }
    if (prefs.get(Prefs.Plugins.VST3_DISCOVERY_ENABLED, null) == null) {
      prefs.putBoolean(Prefs.Plugins.VST3_DISCOVERY_ENABLED, Boolean.TRUE);
    }
    if (prefs.get(Prefs.Plugins.NativeHost.ENABLED, null) == null) {
      prefs.putBoolean(Prefs.Plugins.NativeHost.ENABLED, Boolean.TRUE);
    }
    if (prefs.get(Prefs.Auth.SELECTED_ACCOUNT, null) == null) {
      prefs.putBoolean(Prefs.Auth.SELECTED_ACCOUNT, Boolean.FALSE);
    }
    if (prefs.get(Prefs.Explore.STORE_SUBDIRECTORY_ENABLED, null) == null) {
      prefs.putBoolean(Prefs.Explore.STORE_SUBDIRECTORY_ENABLED, Boolean.TRUE);
    }
    if (prefs.get(Prefs.Plugins.NativeHost.LOADER_TIMEOUT, null) == null) {
      prefs.putLong(Prefs.Plugins.NativeHost.LOADER_TIMEOUT, 10L);
    }
  }

  /**
   * Seeds a default additional scan directory for a plugin format, if none is configured yet.
   *
   * @param prefs        Application preferences
   * @param extraDirKey  Extra directory preference key for the format
   * @param format       Plugin format
   */
  private void seedDefaultExtraDirectory(ApplicationPreferences prefs, String extraDirKey, PluginFormat format) {
    if (prefs.get(extraDirKey, null) == null) {
      String systemPath = this.getApplicationDefaults().getLinuxSystemPluginPath(format);
      if (systemPath != null) {
        prefs.putList(extraDirKey, List.of(systemPath));
      }
    }
  }

  /**
   * Clear all user data including Settings, configured stores and cache.
   */
  public void clearAllUserData() {

    try {
      this.getPreferences().clear();
      pluginRepository.deleteAll();
      packageRepository.deleteAll();
      remoteSourceRepository.deleteAll();
      fileStatRepository.deleteAll();
      projectRepository.deleteAll();

      clearCache();

    } catch (BackingStoreException e) {
      log.error("Preferences cannot be updated", e);
    }
  }

  /**
   * Clear data from all application caches.
   */
  public void clearCache() {
    imageCache.clear();
  }

}
