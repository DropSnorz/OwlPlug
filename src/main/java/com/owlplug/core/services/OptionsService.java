package com.owlplug.core.services;

import com.owlplug.auth.dao.GoogleCredentialDAO;
import com.owlplug.auth.dao.UserAccountDAO;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.dao.PluginDAO;
import com.owlplug.core.dao.PluginRepositoryDAO;
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
public class OptionsService {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private Preferences prefs;
  @Autowired
  private PluginRepositoryDAO pluginRepositoryDAO;
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

    // Init default options
    if (prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, null) == null) {
      prefs.put(ApplicationDefaults.VST_DIRECTORY_KEY, ApplicationDefaults.DEFAULT_VST_DIRECTORY);
    }
    if (prefs.get(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, null) == null) {
      prefs.putBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, Boolean.TRUE);
    }
    if (prefs.get(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, null) == null) {
      prefs.putBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, Boolean.FALSE);
    }
    if (prefs.get(ApplicationDefaults.SELECTED_ACCOUNT_KEY, null) == null) {
      prefs.putBoolean(ApplicationDefaults.SELECTED_ACCOUNT_KEY, Boolean.FALSE);
    }
    if (prefs.get(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, null) == null) {
      prefs.putBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, Boolean.TRUE);
    }
  }

  /**
   * Clear all user data including Options, Configured repositories and cache.
   * 
   * @return true if data has been successfully cleared, false otherwise
   */
  public boolean clearAllUserData() {

    try {
      prefs.clear();
      pluginDAO.deleteAll();
      pluginRepositoryDAO.deleteAll();

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
