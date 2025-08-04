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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Preferences wrapper supporting string list persistence.
 */
@Component
public class ApplicationPreferences {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private Preferences basePreferences;

  public ApplicationPreferences() {
    basePreferences = Preferences.userRoot().node("com.owlplug.user");

  }

  public String get(String key, String def) {
    return basePreferences.get(key, def);
  }

  public void put(String key, String value) {
    basePreferences.put(key, value);
  }

  public boolean getBoolean(String key, boolean def) {
    return basePreferences.getBoolean(key, def);
  }

  public void putBoolean(String key, boolean value) {
    basePreferences.putBoolean(key, value);
  }

  public long getLong(String key, long def) {
    return basePreferences.getLong(key, def);
  }

  public void putLong(String key, long value) {
    basePreferences.putLong(key, value);
  }

  public List<String> getList(String key) {
    return getList(key, new ArrayList<>());
  }

  public List<String> getList(String key, List<String> def) {

    String jsonValue = get(key, null);
    if (jsonValue == null) {
      return def;
    }

    ObjectMapper mapper = new ObjectMapper();
    try {
      String[] values = mapper.readValue(jsonValue, String[].class);
      return Arrays.asList(values);
    } catch (JsonProcessingException e) {
      log.error("List value can't be deserialized from Preferences using key {}: {}", key, jsonValue, e);
      return def;
    }

  }

  public void putList(String key, List<String> value) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String jsonValue = mapper.writeValueAsString(value);
      put(key, jsonValue);
    } catch (JsonProcessingException e) {
      log.error("List value can't be serialized to Preferences using key {}", key, e);
    }
  }

  public void clear() throws BackingStoreException {
    this.basePreferences.clear();
  }

}
