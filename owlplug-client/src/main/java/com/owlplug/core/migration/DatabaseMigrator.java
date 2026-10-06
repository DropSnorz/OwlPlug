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

import com.owlplug.core.components.ApplicationDefaults;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Applies pending {@link DatabaseMigration}s around the Hibernate
 * {@code ddl-auto=update} schema update. Applied versions are tracked in the
 * {@value #MIGRATION_TABLE} table, which is not managed by Hibernate.
 *
 * <ul>
 *   <li>Fresh database: Hibernate creates the current schema, so every known
 *   migration is recorded as applied without being executed (baseline).</li>
 *   <li>Existing database: pending migrations are executed in version order,
 *   per phase. A database created before this mechanism existed has no applied
 *   version, so every migration runs on it.</li>
 * </ul>
 *
 * <p>H2 auto-commits DDL statements, so a failing migration can't be fully
 * rolled back. A database backup is taken before applying pending migrations.
 * </p>
 *
 * <p>{@link #runPreSchemaUpdate()} is triggered before the EntityManagerFactory
 * creation and {@link #runPostSchemaUpdate()} right after it, see
 * {@link DatabaseMigrationConfiguration}.</p>
 */
@Component("databaseMigrator")
public class DatabaseMigrator {

  static final String MIGRATION_TABLE = "OWLPLUG_SCHEMA_MIGRATION";
  static final String BACKUP_PREFIX = "owlplug-db-backup-";
  private static final DateTimeFormatter BACKUP_TIMESTAMP =
      DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS").withZone(ZoneOffset.UTC);

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final DataSource dataSource;
  private final List<DatabaseMigration> migrations;
  private final Path backupDirectory;

  private final Set<Integer> appliedVersions = new HashSet<>();
  private boolean postSchemaUpdateDone = false;
  private Path currentBackup;

  @Autowired
  public DatabaseMigrator(DataSource dataSource, ObjectProvider<DatabaseMigration> migrations) {
    this(dataSource, migrations.stream().toList(), Paths.get(ApplicationDefaults.getUserDataDirectory()));
  }

  /**
   * Creates a migrator.
   *
   * @param dataSource the application datasource
   * @param migrations all known migrations
   * @param backupDirectory directory receiving the database backup taken before
   *     applying migrations, or null to disable backups
   */
  public DatabaseMigrator(DataSource dataSource, List<DatabaseMigration> migrations, Path backupDirectory) {
    this.dataSource = dataSource;
    this.migrations = migrations.stream()
        .sorted(Comparator.comparingInt(DatabaseMigration::getVersion))
        .toList();
    this.backupDirectory = backupDirectory;

    Set<Integer> versions = new HashSet<>();
    for (DatabaseMigration migration : this.migrations) {
      if (!versions.add(migration.getVersion())) {
        throw new IllegalStateException("Duplicate database migration version " + migration.getVersion());
      }
    }
  }

  @PostConstruct
  public void runPreSchemaUpdate() {
    try (Connection connection = dataSource.getConnection()) {
      boolean freshDatabase = !hasApplicationTables(connection);
      createMigrationTable(connection);
      appliedVersions.addAll(loadAppliedVersions(connection));

      List<DatabaseMigration> pending = pendingMigrations(null);
      if (freshDatabase) {
        for (DatabaseMigration migration : pending) {
          recordApplied(connection, migration);
        }
        if (!pending.isEmpty()) {
          log.info("New database, {} migration(s) marked as applied", pending.size());
        }
      } else if (!pending.isEmpty()) {
        log.info("{} pending database migration(s)", pending.size());
        backup(connection);
      }
    } catch (SQLException e) {
      throw new DatabaseMigrationException("Database migration initialization failed",
          DatabaseMigrationException.INIT_STEP, null, e);
    }

    runPhase(MigrationPhase.BEFORE_SCHEMA_UPDATE);
  }

  public void runPostSchemaUpdate() {
    if (!postSchemaUpdateDone) {
      postSchemaUpdateDone = true;
      runPhase(MigrationPhase.AFTER_SCHEMA_UPDATE);
      deletePreviousBackups();
    }
  }

  private void runPhase(MigrationPhase phase) {
    List<DatabaseMigration> pending = pendingMigrations(phase);
    if (pending.isEmpty()) {
      return;
    }

    try (Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(false);
      MigrationContext context = new MigrationContext(connection);
      for (DatabaseMigration migration : pending) {
        try {
          migration.migrate(context);
          recordApplied(connection, migration);
          connection.commit();
          log.info("Applied database migration {} ({}): {}", migration.getVersion(), phase,
              migration.getDescription());
        } catch (SQLException | RuntimeException e) {
          connection.rollback();
          throw new DatabaseMigrationException("Database migration " + migration.getVersion()
              + " failed: " + migration.getDescription(), phase.name(), migration.getVersion(), e);
        }
      }
    } catch (SQLException e) {
      throw new DatabaseMigrationException("Database migration phase " + phase + " failed",
          phase.name(), null, e);
    }
  }

  private List<DatabaseMigration> pendingMigrations(MigrationPhase phase) {
    return migrations.stream()
        .filter(m -> !appliedVersions.contains(m.getVersion()))
        .filter(m -> phase == null || m.getPhase() == phase)
        .toList();
  }

  private boolean hasApplicationTables(Connection connection) throws SQLException {
    String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME <> ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, MIGRATION_TABLE);
      try (ResultSet rs = statement.executeQuery()) {
        return rs.next() && rs.getLong(1) > 0;
      }
    }
  }

  private void createMigrationTable(Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute("CREATE TABLE IF NOT EXISTS " + MIGRATION_TABLE
          + " (VERSION INT PRIMARY KEY, DESCRIPTION VARCHAR(255), APPLIED_ON TIMESTAMP)");
    }
  }

  private Set<Integer> loadAppliedVersions(Connection connection) throws SQLException {
    Set<Integer> versions = new HashSet<>();
    try (Statement statement = connection.createStatement();
         ResultSet rs = statement.executeQuery("SELECT VERSION FROM " + MIGRATION_TABLE)) {
      while (rs.next()) {
        versions.add(rs.getInt(1));
      }
    }
    return versions;
  }

  private void recordApplied(Connection connection, DatabaseMigration migration) throws SQLException {
    String sql = "INSERT INTO " + MIGRATION_TABLE + " (VERSION, DESCRIPTION, APPLIED_ON) VALUES (?, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, migration.getVersion());
      statement.setString(2, migration.getDescription());
      statement.setTimestamp(3, Timestamp.from(Instant.now()));
      statement.executeUpdate();
    }
    appliedVersions.add(migration.getVersion());
  }

  /**
   * Takes an online backup of the database using the H2 BACKUP command. Each
   * backup gets a unique name so that a retry after a failed migration never
   * overwrites the backup taken before the database was partially migrated.
   */
  private void backup(Connection connection) throws SQLException {
    if (backupDirectory == null) {
      return;
    }
    int currentVersion = appliedVersions.stream().mapToInt(Integer::intValue).max().orElse(0);
    String baseName = BACKUP_PREFIX + "v" + currentVersion + "-" + BACKUP_TIMESTAMP.format(Instant.now());
    Path backupFile = backupDirectory.resolve(baseName + ".zip");
    for (int i = 1; Files.exists(backupFile); i++) {
      backupFile = backupDirectory.resolve(baseName + "-" + i + ".zip");
    }

    try (Statement statement = connection.createStatement()) {
      statement.execute("BACKUP TO '" + backupFile.toAbsolutePath().toString().replace("'", "''") + "'");
    }
    currentBackup = backupFile;
    log.info("Database backup created before migration: {}", backupFile);
  }

  /**
   * Keeps only the backup taken by this run. Called once every migration has
   * succeeded, so earlier backups are kept as long as a migration keeps failing.
   */
  private void deletePreviousBackups() {
    if (currentBackup == null) {
      return;
    }
    try (DirectoryStream<Path> backups = Files.newDirectoryStream(backupDirectory, BACKUP_PREFIX + "*.zip")) {
      for (Path previousBackup : backups) {
        if (!previousBackup.getFileName().equals(currentBackup.getFileName())) {
          Files.deleteIfExists(previousBackup);
        }
      }
    } catch (IOException e) {
      log.warn("Previous database backups could not be deleted", e);
    }
  }

}
