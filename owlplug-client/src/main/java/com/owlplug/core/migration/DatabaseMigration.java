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

package com.owlplug.core.migration;

import java.sql.SQLException;

/**
 * A versioned database migration, applied once per workspace database.
 * Implementations are Spring components discovered by {@link DatabaseMigrator}.
 * See the {@code com.owlplug.core.migration.versions} package
 * documentation for authoring rules.
 */
public interface DatabaseMigration {

  /**
   * Unique and strictly increasing migration version. Never reuse or change
   * the version of a released migration.
   */
  int getVersion();

  String getDescription();

  default MigrationPhase getPhase() {
    return MigrationPhase.AFTER_SCHEMA_UPDATE;
  }

  void migrate(MigrationContext context) throws SQLException;

}
