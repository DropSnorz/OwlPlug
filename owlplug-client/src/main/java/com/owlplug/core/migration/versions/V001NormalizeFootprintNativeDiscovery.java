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

package com.owlplug.core.migration.versions;

import com.owlplug.core.migration.DatabaseMigration;
import com.owlplug.core.migration.MigrationContext;
import com.owlplug.core.migration.MigrationPhase;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * First migration, kept as a reference implementation. It is transparent for
 * existing data: native discovery is mapped to a primitive boolean, so no
 * footprint is expected to have a null value.
 */
@Component
public class V001NormalizeFootprintNativeDiscovery implements DatabaseMigration {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public int getVersion() {
    return 1;
  }

  @Override
  public String getDescription() {
    return "Default null PLUGIN_FOOTPRINT.NATIVE_DISCOVERY_ENABLED to true";
  }

  @Override
  public MigrationPhase getPhase() {
    return MigrationPhase.AFTER_SCHEMA_UPDATE;
  }

  @Override
  public void migrate(MigrationContext context) throws SQLException {
    if (!context.columnExists("PLUGIN_FOOTPRINT", "NATIVE_DISCOVERY_ENABLED")) {
      return;
    }
    try (Statement statement = context.getConnection().createStatement()) {
      int updated = statement.executeUpdate("UPDATE PLUGIN_FOOTPRINT SET NATIVE_DISCOVERY_ENABLED = TRUE "
          + "WHERE NATIVE_DISCOVERY_ENABLED IS NULL");
      log.debug("{} plugin footprint(s) updated", updated);
    }
  }

}
