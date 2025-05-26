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

import jakarta.annotation.PreDestroy;
import java.util.Map;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The PersistentKeyValueStore is a key-value based storage manager
 * to persist user data in the owlplug workspace (usually ~/.owlplug).
 * The owlplug default data-source is an H2 SQL database semi-persistent.
 * A relational database allow for some query optimization and full JPA / Spring data support.
 * Because schema migration is not managed by OwlPlug, the database is cleared
 * when breaking changes or changes not managed by the hibernate ddl-update parameter are introduced.
 * All data that need to persist between OwlPlug updates must be stored in this KeyValue Store.
 */
@Component
public class PersistentKeyValueStore {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private final MVStore store;

  public PersistentKeyValueStore() {
    store = new MVStore.Builder()
            // TODO configure from application properties
            .fileName("~/.owlplug/kvstore.mv.db")
            .open();
  }

  public void put(String mapName, String key, String value) {
    MVMap<String, String> map = store.openMap(mapName);
    map.put(key,value);
    store.commit();
  }

  public void putAll(String mapName, Map<String, String> valueMap) {
    MVMap<String, String> map = store.openMap(mapName);
    map.putAll(valueMap);
    store.commit();
  }

  public String get(String mapName, String key) {
    MVMap<String, String> map = store.openMap(mapName);
    return map.get(key);
  }

  @PreDestroy
  public void destroy() {
    log.info("Closing persistent key value store, PreDestroy signal received.");
    store.close();
  }
}
