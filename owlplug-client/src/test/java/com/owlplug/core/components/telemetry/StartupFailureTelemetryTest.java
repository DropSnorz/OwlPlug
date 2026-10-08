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

package com.owlplug.core.components.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.core.migration.DatabaseMigrationException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;

public class StartupFailureTelemetryTest {

  @Test
  public void testShouldDetectAlreadyRunningPhaseFromHibernateRootCause() {
    HibernateException rootCause = new HibernateException("Database may be already in use");
    BeanCreationException ex = new BeanCreationException("dataSource", "Failed", rootCause);

    assertEquals("already_running", StartupFailureTelemetry.determinePhase(ex));
  }

  @Test
  public void testShouldDetectBeanCreationPhaseForOtherRootCauses() {
    IllegalStateException rootCause = new IllegalStateException("boom");
    BeanCreationException ex = new BeanCreationException("someBean", "Failed", rootCause);

    assertEquals("bean_creation", StartupFailureTelemetry.determinePhase(ex));
  }

  @Test
  public void testShouldDetectGenericPhaseForNonBeanCreationExceptions() {
    RuntimeException ex = new RuntimeException("unexpected");

    assertEquals("generic", StartupFailureTelemetry.determinePhase(ex));
  }

  @Test
  public void testShouldReportRootCauseClassWhenDifferentFromErrorClass() {
    IllegalStateException rootCause = new IllegalStateException("boom");
    BeanCreationException ex = new BeanCreationException("someBean", "Failed", rootCause);

    assertEquals("IllegalStateException", StartupFailureTelemetry.rootCauseClassName(ex));
  }

  @Test
  public void testShouldReportRootCauseMessageWhenDifferentFromErrorClass() {
    IllegalStateException rootCause = new IllegalStateException("boom");
    BeanCreationException ex = new BeanCreationException("someBean", "Failed", rootCause);

    assertEquals("boom", StartupFailureTelemetry.rootCauseMessage(ex));
  }

  @Test
  public void testShouldReportRootCauseForNonBeanCreationExceptionsToo() {
    // Root cause detection isn't limited to BeanCreationException: any exception with a
    // wrapped cause (e.g. FXML loading / MainController lookup failures) should still surface
    // rootCauseClass/summary.
    IllegalStateException rootCause = new IllegalStateException("disk full");
    RuntimeException ex = new RuntimeException("could not load resource", rootCause);

    assertEquals("IllegalStateException", StartupFailureTelemetry.rootCauseClassName(ex));
    assertEquals("disk full", StartupFailureTelemetry.rootCauseMessage(ex));
  }

  @Test
  public void testShouldOmitRootCauseClassWhenNoCauseChain() {
    RuntimeException ex = new RuntimeException("unexpected");

    assertNull(StartupFailureTelemetry.rootCauseClassName(ex));
  }

  @Test
  public void testShouldOmitRootCauseMessageWhenNoCauseChain() {
    RuntimeException ex = new RuntimeException("unexpected");

    assertNull(StartupFailureTelemetry.rootCauseMessage(ex));
  }

  @Test
  public void testShouldRedactJdbcUrlInErrorMessage() {
    Map<String, String> params = new HashMap<>();
    params.put("error", "Timed out waiting for lock at jdbc:h2:file:/home/user/.owlplug/owlplug;IFEXISTS=TRUE");

    TelemetryReporter.sanitize(params);

    // The "jdbc:h2:file:" prefix isn't a recognized path pattern itself, so it survives; the
    // path portion starting at the first "/" is redacted (confirms the caveat noted in the plan:
    // the H2 lock-file path is caught, but scheme prefixes like "jdbc:h2:file:" are not).
    assertEquals("Timed out waiting for lock at jdbc:h2:file:<path>", params.get("error"));
  }

  @Test
  public void testShouldDetectDatabaseMigrationPhaseBeforeHibernateRootCause() {
    HibernateException rootCause = new HibernateException("Database may be already in use");
    DatabaseMigrationException migrationEx = new DatabaseMigrationException("Database migration 3 failed: test",
        "BEFORE_SCHEMA_UPDATE", 3, rootCause);
    BeanCreationException ex = new BeanCreationException("databaseMigrator", "Failed", migrationEx);

    assertEquals("database_migration", StartupFailureTelemetry.determinePhase(ex));
  }

  @Test
  public void testShouldReportMigrationDetails() {
    SQLException rootCause = new SQLException("Column not found");
    DatabaseMigrationException migrationEx = new DatabaseMigrationException("Database migration 3 failed: test",
        "AFTER_SCHEMA_UPDATE", 3, rootCause);
    BeanCreationException ex = new BeanCreationException("entityManagerFactory", "Failed", migrationEx);
    Map<String, String> params = new HashMap<>();

    StartupFailureTelemetry.putMigrationDetails(params, ex);

    assertEquals("AFTER_SCHEMA_UPDATE", params.get("migrationStep"));
    assertEquals("3", params.get("migrationVersion"));
    assertEquals("Database migration 3 failed: test", params.get("migrationError"));
  }

  @Test
  public void testShouldOmitMigrationVersionForInitFailure() {
    DatabaseMigrationException migrationEx = new DatabaseMigrationException("Database migration initialization failed",
        DatabaseMigrationException.INIT_STEP, null, new SQLException("boom"));
    Map<String, String> params = new HashMap<>();

    StartupFailureTelemetry.putMigrationDetails(params, migrationEx);

    assertEquals("INIT", params.get("migrationStep"));
    assertFalse(params.containsKey("migrationVersion"));
  }

  @Test
  public void testShouldNotReportMigrationDetailsForOtherFailures() {
    BeanCreationException ex = new BeanCreationException("someBean", "Failed", new IllegalStateException("boom"));
    Map<String, String> params = new HashMap<>();

    StartupFailureTelemetry.putMigrationDetails(params, ex);

    assertTrue(params.isEmpty());
  }
}
