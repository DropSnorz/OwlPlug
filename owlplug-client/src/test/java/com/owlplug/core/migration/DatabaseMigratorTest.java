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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DatabaseMigratorTest {

  private DataSource dataSource;
  private List<String> executionLog;

  @BeforeEach
  public void setUp() {
    dataSource = dataSource("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
    executionLog = new ArrayList<>();
  }

  @Test
  public void testShouldBaselineFreshDatabaseWithoutRunningMigrations() throws SQLException {
    DatabaseMigrator migrator = new DatabaseMigrator(dataSource, List.of(
        migration(1, MigrationPhase.BEFORE_SCHEMA_UPDATE),
        migration(2, MigrationPhase.AFTER_SCHEMA_UPDATE)), null);

    migrator.runPreSchemaUpdate();
    migrator.runPostSchemaUpdate();

    assertTrue(executionLog.isEmpty());
    assertEquals(List.of(1, 2), appliedVersions());
  }

  @Test
  public void testShouldRunPendingMigrationsInVersionOrderPerPhase() throws SQLException {
    createApplicationTable();
    DatabaseMigrator migrator = new DatabaseMigrator(dataSource, List.of(
        migration(3, MigrationPhase.BEFORE_SCHEMA_UPDATE),
        migration(1, MigrationPhase.AFTER_SCHEMA_UPDATE),
        migration(2, MigrationPhase.BEFORE_SCHEMA_UPDATE)), null);

    migrator.runPreSchemaUpdate();
    assertEquals(List.of("2", "3"), executionLog);

    migrator.runPostSchemaUpdate();
    assertEquals(List.of("2", "3", "1"), executionLog);
    assertEquals(List.of(1, 2, 3), appliedVersions());
  }

  @Test
  public void testShouldNotRunAppliedMigrationsAgain() throws SQLException {
    createApplicationTable();
    List<DatabaseMigration> migrations = List.of(migration(1, MigrationPhase.AFTER_SCHEMA_UPDATE));

    DatabaseMigrator firstRun = new DatabaseMigrator(dataSource, migrations, null);
    firstRun.runPreSchemaUpdate();
    firstRun.runPostSchemaUpdate();

    DatabaseMigrator secondRun = new DatabaseMigrator(dataSource, migrations, null);
    secondRun.runPreSchemaUpdate();
    secondRun.runPostSchemaUpdate();

    assertEquals(List.of("1"), executionLog);
  }

  @Test
  public void testShouldRollbackAndNotRecordFailingMigration() throws SQLException {
    createApplicationTable();
    DatabaseMigration failing = new TestMigration(1, MigrationPhase.BEFORE_SCHEMA_UPDATE) {
      @Override
      public void migrate(MigrationContext context) throws SQLException {
        context.execute("INSERT INTO APP_TABLE (ID) VALUES (1)");
        throw new SQLException("boom");
      }
    };
    DatabaseMigrator migrator = new DatabaseMigrator(dataSource, List.of(failing), null);

    DatabaseMigrationException ex = assertThrows(DatabaseMigrationException.class, migrator::runPreSchemaUpdate);
    assertEquals(1, ex.getVersion());
    assertEquals(MigrationPhase.BEFORE_SCHEMA_UPDATE.name(), ex.getStep());
    assertTrue(appliedVersions().isEmpty());
    assertEquals(0, count("SELECT COUNT(*) FROM APP_TABLE"));
  }

  @Test
  public void testShouldRejectDuplicateVersions() {
    List<DatabaseMigration> migrations = List.of(
        migration(1, MigrationPhase.BEFORE_SCHEMA_UPDATE),
        migration(1, MigrationPhase.AFTER_SCHEMA_UPDATE));

    assertThrows(IllegalStateException.class, () -> new DatabaseMigrator(dataSource, migrations, null));
  }

  @Test
  public void testShouldDetectTablesAndColumns() throws SQLException {
    createApplicationTable();
    try (Connection connection = dataSource.getConnection()) {
      MigrationContext context = new MigrationContext(connection);
      assertTrue(context.tableExists("app_table"));
      assertTrue(context.columnExists("app_table", "id"));
      assertFalse(context.columnExists("app_table", "missing"));
      assertFalse(context.tableExists("missing"));
    }
  }

  @Test
  public void testShouldKeepOnlyLatestBackupAfterSuccessfulMigration(@TempDir Path tempDir)
      throws SQLException, IOException {
    dataSource = dataSource("jdbc:h2:file:" + tempDir.resolve("db").toAbsolutePath());
    createApplicationTable();
    Files.createFile(tempDir.resolve(DatabaseMigrator.BACKUP_PREFIX + "stale.zip"));

    DatabaseMigrator firstRun = new DatabaseMigrator(dataSource,
        List.of(migration(1, MigrationPhase.AFTER_SCHEMA_UPDATE)), tempDir);
    firstRun.runPreSchemaUpdate();
    firstRun.runPostSchemaUpdate();
    DatabaseMigrator secondRun = new DatabaseMigrator(dataSource, List.of(
        migration(1, MigrationPhase.AFTER_SCHEMA_UPDATE),
        migration(2, MigrationPhase.BEFORE_SCHEMA_UPDATE)), tempDir);
    secondRun.runPreSchemaUpdate();
    assertEquals(2, backups(tempDir).size());
    secondRun.runPostSchemaUpdate();

    List<Path> backups = backups(tempDir);
    assertEquals(1, backups.size());
    assertTrue(backups.get(0).getFileName().toString().startsWith(DatabaseMigrator.BACKUP_PREFIX + "v1-"));
    assertTrue(Files.size(backups.get(0)) > 0);
  }

  @Test
  public void testShouldKeepExistingBackupsWhenMigrationFails(@TempDir Path tempDir)
      throws SQLException, IOException {
    dataSource = dataSource("jdbc:h2:file:" + tempDir.resolve("db").toAbsolutePath());
    createApplicationTable();
    DatabaseMigration failing = new TestMigration(1, MigrationPhase.BEFORE_SCHEMA_UPDATE) {
      @Override
      public void migrate(MigrationContext context) throws SQLException {
        throw new SQLException("boom");
      }
    };

    for (int run = 0; run < 2; run++) {
      DatabaseMigrator migrator = new DatabaseMigrator(dataSource, List.of(failing), tempDir);
      assertThrows(DatabaseMigrationException.class, migrator::runPreSchemaUpdate);
    }

    assertEquals(2, backups(tempDir).size());
  }

  private List<Path> backups(Path directory) throws IOException {
    try (Stream<Path> files = Files.list(directory)) {
      return files.filter(p -> p.getFileName().toString().startsWith(DatabaseMigrator.BACKUP_PREFIX))
          .toList();
    }
  }

  private static DataSource dataSource(String url) {
    JdbcDataSource h2 = new JdbcDataSource();
    h2.setURL(url);
    h2.setUser("sa");
    return h2;
  }

  private void createApplicationTable() throws SQLException {
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("CREATE TABLE APP_TABLE (ID BIGINT PRIMARY KEY)");
    }
  }

  private List<Integer> appliedVersions() throws SQLException {
    List<Integer> versions = new ArrayList<>();
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement();
         ResultSet rs = statement.executeQuery("SELECT VERSION FROM " + DatabaseMigrator.MIGRATION_TABLE
             + " ORDER BY VERSION")) {
      while (rs.next()) {
        versions.add(rs.getInt(1));
      }
    }
    return versions;
  }

  private long count(String sql) throws SQLException {
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement();
         ResultSet rs = statement.executeQuery(sql)) {
      rs.next();
      return rs.getLong(1);
    }
  }

  private DatabaseMigration migration(int version, MigrationPhase phase) {
    return new TestMigration(version, phase);
  }

  private class TestMigration implements DatabaseMigration {

    private final int version;
    private final MigrationPhase phase;

    TestMigration(int version, MigrationPhase phase) {
      this.version = version;
      this.phase = phase;
    }

    @Override
    public int getVersion() {
      return version;
    }

    @Override
    public String getDescription() {
      return "Test migration " + version;
    }

    @Override
    public MigrationPhase getPhase() {
      return phase;
    }

    @Override
    public void migrate(MigrationContext context) throws SQLException {
      executionLog.add(String.valueOf(version));
    }
  }

}
