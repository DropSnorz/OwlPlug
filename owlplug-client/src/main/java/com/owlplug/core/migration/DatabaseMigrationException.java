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
 * Thrown when the database migration process fails, aborting application startup.
 */
public class DatabaseMigrationException extends RuntimeException {

  /**
   * Step that failed: a {@link MigrationPhase} name, or {@value #INIT_STEP} for the
   * migration table setup and database backup.
   */
  public static final String INIT_STEP = "INIT";

  private final String step;
  private final Integer version;

  public DatabaseMigrationException(String message, String step, Integer version, Throwable cause) {
    super(message, cause);
    this.step = step;
    this.version = version;
  }

  public String getStep() {
    return step;
  }

  /**
   * Version of the failing migration, or null when the failure is not tied to
   * a specific migration.
   */
  public Integer getVersion() {
    return version;
  }

}
