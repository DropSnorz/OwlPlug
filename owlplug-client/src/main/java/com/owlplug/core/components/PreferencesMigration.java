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

package com.owlplug.core.components;

import com.owlplug.core.components.ApplicationDefaults.Prefs;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Migrates legacy flat preference keys to the current hierarchical key scheme.
 * Runs once at startup before OptionsService initializes defaults.
 * Bump MIGRATION_VERSION whenever new entries are added to MIGRATIONS.
 */
@Component
public class PreferencesMigration {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private static final String MIGRATION_VERSION_KEY = "app.migration_version";
  private static final int MIGRATION_VERSION = 2;

  private static final Map<String, String> MIGRATIONS = Map.ofEntries(
    Map.entry("VST_DIRECTORY",             Prefs.Plugins.VST2_DIRECTORY),
    Map.entry("VST2_DISCOVERY_ENABLED",    Prefs.Plugins.VST2_DISCOVERY_ENABLED),
    Map.entry("VST2_EXTRA_DIRECTORY_KEY",  Prefs.Plugins.VST2_EXTRA_DIRECTORY),
    Map.entry("VST3_DIRECTORY",            Prefs.Plugins.VST3_DIRECTORY),
    Map.entry("VST3_DISCOVERY_ENABLED",    Prefs.Plugins.VST3_DISCOVERY_ENABLED),
    Map.entry("VST3_EXTRA_DIRECTORY_KEY",  Prefs.Plugins.VST3_EXTRA_DIRECTORY),
    Map.entry("AU_DIRECTORY_KEY",          Prefs.Plugins.AU_DIRECTORY),
    Map.entry("AU_DISCOVERY_ENABLED_KEY",  Prefs.Plugins.AU_DISCOVERY_ENABLED),
    Map.entry("AU_EXTRA_DIRECTORY_KEY",    Prefs.Plugins.AU_EXTRA_DIRECTORY),
    Map.entry("LV2_DIRECTORY_KEY",         Prefs.Plugins.LV2_DIRECTORY),
    Map.entry("LV2_DISCOVERY_ENABLED_KEY", Prefs.Plugins.LV2_DISCOVERY_ENABLED),
    Map.entry("LV2_EXTRA_DIRECTORY_KEY",   Prefs.Plugins.LV2_EXTRA_DIRECTORY),
    Map.entry("NATIVE_HOST_ENABLED_KEY",   Prefs.Plugins.NativeHost.ENABLED),
    Map.entry("PREFERRED_NATIVE_LOADER",   Prefs.Plugins.NativeHost.PREFERRED_LOADER),
    Map.entry("NATIVE_LOADER_TIMEOUT_KEY", Prefs.Plugins.NativeHost.LOADER_TIMEOUT),
    Map.entry("SELECTED_ACCOUNT_KEY",      Prefs.Auth.SELECTED_ACCOUNT),
    Map.entry("SYNC_PLUGINS_STARTUP_KEY",  Prefs.App.SYNC_PLUGINS_ON_STARTUP),
    Map.entry("STORE_DIRECTORY_ENABLED_KEY",  Prefs.Explore.STORE_DIRECTORY_ENABLED),
    Map.entry("STORE_BY_CREATOR_ENABLED_KEY", Prefs.Explore.STORE_BY_CREATOR_ENABLED),
    Map.entry("STORE_DIRECTORY_KEY",          Prefs.Explore.STORE_DIRECTORY),
    Map.entry("STORE_SUBDIRECTORY_ENABLED",   Prefs.Explore.STORE_SUBDIRECTORY_ENABLED),
    Map.entry("FIRST_LAUNCH_KEY",             Prefs.App.FIRST_LAUNCH),
    Map.entry("APPLICATION_STATE_KEY",        Prefs.App.STATE),
    Map.entry("SHOW_DIALOG_DISABLE_PLUGIN_KEY", Prefs.App.SHOW_DIALOG_DISABLE_PLUGIN),
    Map.entry("PROJECT_DIRECTORY_KEY",        Prefs.Projects.DIRECTORY),
    Map.entry("PLUGIN_PREFERRED_DISPLAY_KEY", Prefs.Plugins.PREFERRED_DISPLAY),
    Map.entry("SYNC_FILE_STAT_KEY",           Prefs.App.SYNC_FILE_STAT),
    Map.entry("TELEMETRY_ENABLED_KEY",        Prefs.Telemetry.ENABLED),
    Map.entry("TELEMETRY_USER_ID_KEY",        Prefs.Telemetry.USER_ID),
    Map.entry("LAST_PLUGIN_SCAN_DATE_KEY",    Prefs.Plugins.LAST_SCAN_DATE)
  );

  private final Preferences prefs = Preferences.userRoot().node("com.owlplug.user");

  @PostConstruct
  public void migrate() {
    int appliedVersion = prefs.getInt(MIGRATION_VERSION_KEY, 0);
    if (appliedVersion >= MIGRATION_VERSION) {
      return;
    }

    MIGRATIONS.forEach((oldKey, newKey) -> {
      String value = prefs.get(oldKey, null);
      if (value != null && prefs.get(newKey, null) == null) {
        prefs.put(newKey, value);
        prefs.remove(oldKey);
        log.debug("Migrated preference key {} -> {}", oldKey, newKey);
      }
    });

    prefs.putInt(MIGRATION_VERSION_KEY, MIGRATION_VERSION);
    log.info("Preferences migrated to version {}", MIGRATION_VERSION);
  }
}
