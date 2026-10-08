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

/**
 * Execution phase of a {@link DatabaseMigration}, relative to the Hibernate
 * {@code ddl-auto=update} schema update.
 */
public enum MigrationPhase {

  /**
   * Runs on the previous schema, before Hibernate updates it. Use it for changes
   * that {@code ddl-auto=update} can't handle: renaming tables or columns, changing
   * column types, dropping obsolete columns or constraints.
   */
  BEFORE_SCHEMA_UPDATE,

  /**
   * Runs right after Hibernate has created new tables and columns, before any
   * application bean accesses the database. Use it for data backfills or to move
   * data into newly created structures.
   */
  AFTER_SCHEMA_UPDATE

}
