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

package com.owlplug;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

/**
 * A Custom H2 Dialect with disabled check for enumerated values.
 * Since Hibernate 6, checks constraints are added to enumerated fields.
 * These checks are not updated with ddl-auto: update. Owlplug rely on
 * this feature to update the schema to avoid using a more complex solution
 * like Liquibase or flyway.
 * Disabling the checks is a hack to allows adding new entries to enum
 * without breaking the SQL schema.
 */
public class OwlPlugH2Dialect extends H2Dialect {

  public OwlPlugH2Dialect(DialectResolutionInfo info) {
    super(info);
  }

  public OwlPlugH2Dialect() {
    super();
  }

  public OwlPlugH2Dialect(DatabaseVersion version) {
    super(version);
  }

  @Override
  public String getCheckCondition(String columnName, String[] values) {
    // Disable check conditions
    return null;
  }

  public String getCheckCondition(String columnName, long min, long max) {
    return null;
  }
}
