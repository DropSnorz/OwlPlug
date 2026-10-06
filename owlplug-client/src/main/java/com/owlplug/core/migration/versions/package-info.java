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

/**
 * Database migrations, applied once per workspace database by
 * {@link com.owlplug.core.migration.DatabaseMigrator}.
 *
 * <p>Hibernate {@code ddl-auto=update} remains responsible for additive schema
 * changes (new tables, columns and indexes). A migration is only needed when
 * existing data must be preserved or transformed, or when something must be
 * renamed, retyped or dropped.</p>
 *
 * <p>Authoring rules:</p>
 * <ul>
 *   <li>One {@code @Component} class per migration, named {@code V<version><Description>}
 *   with a zero-padded version (e.g. {@code V003AddFootprintNote}), using the next free
 *   version number. Never modify a released migration.</li>
 *   <li>Pick the phase: {@code BEFORE_SCHEMA_UPDATE} for renames, type changes and
 *   drops on the previous schema; {@code AFTER_SCHEMA_UPDATE} (default) for data
 *   backfills into structures Hibernate just created.</li>
 *   <li>Never create a table or column that is mapped by an entity; Hibernate owns them.</li>
 *   <li>Only rely on table and column names, never on Hibernate generated constraint
 *   or foreign key names, which may change between Hibernate versions.</li>
 *   <li>Be defensive with {@code tableExists} / {@code columnExists}: databases created
 *   before the migration mechanism run every migration.</li>
 * </ul>
 *
 * <p>See {@link com.owlplug.core.migration.versions.V001NormalizeFootprintNativeDiscovery} for a data migration. Schema example:</p>
 * <pre>{@code
 * @Component
 * public class V002RenameFootprintComment implements DatabaseMigration {
 *
 *   public int getVersion() {
 *     return 2;
 *   }
 *
 *   public String getDescription() {
 *     return "Rename PLUGIN_FOOTPRINT.COMMENT to NOTE";
 *   }
 *
 *   public MigrationPhase getPhase() {
 *     return MigrationPhase.BEFORE_SCHEMA_UPDATE;
 *   }
 *
 *   public void migrate(MigrationContext context) throws SQLException {
 *     if (context.columnExists("PLUGIN_FOOTPRINT", "COMMENT")) {
 *       context.execute("ALTER TABLE PLUGIN_FOOTPRINT ALTER COLUMN COMMENT RENAME TO NOTE");
 *     }
 *   }
 * }
 * }</pre>
 */
package com.owlplug.core.migration.versions;
