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
 
package com.owlplug.core.services;

import com.owlplug.auth.dao.GoogleCredentialDAO;
import com.owlplug.auth.dao.UserAccountDAO;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.dao.PluginDAO;
import com.owlplug.store.dao.StoreDAO;
import com.owlplug.store.dao.StoreProductDAO;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
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
    
    Preferences prefs = this.getPreferences();
    // Init default options
    if (prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, null) == null) {
      prefs.put(ApplicationDefaults.VST_DIRECTORY_KEY, ApplicationDefaults.DEFAULT_VST_DIRECTORY);
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
