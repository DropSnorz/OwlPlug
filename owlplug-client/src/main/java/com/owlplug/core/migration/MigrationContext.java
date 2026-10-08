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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

/**
 * JDBC helpers given to {@link DatabaseMigration#migrate(MigrationContext)}.
 * Table and column names are resolved case-insensitively in the PUBLIC schema.
 */
public class MigrationContext {

  private static final String SCHEMA = "PUBLIC";

  private final Connection connection;

  public MigrationContext(Connection connection) {
    this.connection = connection;
  }

  public Connection getConnection() {
    return connection;
  }

  public void execute(String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute(sql);
    }
  }

  public boolean tableExists(String table) throws SQLException {
    String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, SCHEMA);
      statement.setString(2, table.toUpperCase(Locale.ROOT));
      return count(statement) > 0;
    }
  }

  public boolean columnExists(String table, String column) throws SQLException {
    String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
        + "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, SCHEMA);
      statement.setString(2, table.toUpperCase(Locale.ROOT));
      statement.setString(3, column.toUpperCase(Locale.ROOT));
      return count(statement) > 0;
    }
  }

  private static long count(PreparedStatement statement) throws SQLException {
    try (ResultSet rs = statement.executeQuery()) {
      return rs.next() ? rs.getLong(1) : 0;
    }
  }

}
