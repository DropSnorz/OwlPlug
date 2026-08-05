package com.owlplug.core.components.telemetry;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
}
