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

import com.owlplug.auth.dao.GoogleCredentialDAO;
import com.owlplug.auth.dao.UserAccountDAO;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ApplicationPreferences;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.dao.PluginDAO;
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.platform.OperatingSystem;
import com.owlplug.store.dao.StoreDAO;
import com.owlplug.store.dao.StoreProductDAO;
import java.util.prefs.BackingStoreException;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OptionsService extends BaseService {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private PluginDAO pluginDAO;
  @Autowired
  private UserAccountDAO userAccountDAO;
  @Autowired
  private GoogleCredentialDAO googleCredentialDAO;
  @Autowired
  private StoreDAO storeDAO;
  @Autowired
  private StoreProductDAO productDAO;
  @Autowired
  private ImageCache imageCache;

  @PostConstruct
  private void initialize() {
    
    ApplicationPreferences prefs = this.getPreferences();
    // Init default options
    if (prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, null) == null) {
      prefs.put(ApplicationDefaults.VST_DIRECTORY_KEY, 
          this.getApplicationDefaults().getDefaultPluginPath(PluginFormat.VST2));
    }
    if (prefs.get(ApplicationDefaults.VST3_DIRECTORY_KEY, null) == null) {
      prefs.put(ApplicationDefaults.VST3_DIRECTORY_KEY, 
          this.getApplicationDefaults().getDefaultPluginPath(PluginFormat.VST3));
    }
    if (prefs.get(ApplicationDefaults.AU_DIRECTORY_KEY, null) == null
        && this.getApplicationDefaults().getRuntimePlatform().getOperatingSystem().equals(OperatingSystem.MAC)) {
      prefs.put(ApplicationDefaults.AU_DIRECTORY_KEY, 
          this.getApplicationDefaults().getDefaultPluginPath(PluginFormat.AU));
    }
    if (prefs.get(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, null) == null) {
      prefs.putBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, Boolean.TRUE);
    }
    if (prefs.get(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, null) == null) {
      prefs.putBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, Boolean.TRUE);
    }
    if (prefs.get(ApplicationDefaults.NATIVE_HOST_ENABLED_KEY, null) == null) {
      prefs.putBoolean(ApplicationDefaults.NATIVE_HOST_ENABLED_KEY, Boolean.TRUE);
    }
    if (prefs.get(ApplicationDefaults.SELECTED_ACCOUNT_KEY, null) == null) {
      prefs.putBoolean(ApplicationDefaults.SELECTED_ACCOUNT_KEY, Boolean.FALSE);
    }
    if (prefs.get(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, null) == null) {
      prefs.putBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, Boolean.TRUE);
    }
  }

  /**
   * Clear all user data including Options, configured stores and cache.
   * 
   * @return true if data has been successfully cleared, false otherwise
   */
  public boolean clearAllUserData() {

    try {
      this.getPreferences().clear();
      pluginDAO.deleteAll();

      googleCredentialDAO.deleteAll();
      userAccountDAO.deleteAll();
      productDAO.deleteAll();
      storeDAO.deleteAll();

      clearCache();

      return true;

    } catch (BackingStoreException e) {
      log.error("Preferences cannot be updated", e);
      return false;
    }
  }

  /**
   * Clear data from all application caches.
   */
  public void clearCache() {
    imageCache.clear();
  }

}
