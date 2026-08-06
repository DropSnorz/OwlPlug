package com.owlplug.core.components.telemetry;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TelemetryReporterTest {

  @Test
  public void testShouldRedactUnixAbsolutePath() {
    Map<String, String> params = new HashMap<>();
    params.put("key", "Error reading /var/log/app/error.log file");

    TelemetryReporter.sanitize(params);

    assertEquals("Error reading <path> file", params.get("key"));
  }

  @Test
  public void testShouldRedactWindowsAbsolutePath() {
    Map<String, String> params = new HashMap<>();
    params.put("key", "Failed at C:\\\\Users\\\\john\\\\secret.txt");

    TelemetryReporter.sanitize(params);

    assertEquals("Failed at <path>", params.get("key"));
  }

  @Test
  public void testShouldNotRedactClassNamesOrPackages() {
    Map<String, String> params = new HashMap<>();
    params.put("key", "at com.example.service.MyClass.method(MyClass.java:42)");

    TelemetryReporter.sanitize(params);

    assertEquals(
            "at com.example.service.MyClass.method(MyClass.java:42)",
            params.get("key")
    );
  }

  @Test
  public void testShouldTruncateLongValuesAndAppendEllipsis() {
    Map<String, String> params = new HashMap<>();
    params.put("key", "abcdefghijklmnopqrstuvwxyz");

    TelemetryReporter.sanitize(params, 20);

    assertEquals("abcdefghijklmnopqrst…", params.get("key"));
  }

  @Test
  public void testShouldHandleMultipleEntriesIndependently() {
    Map<String, String> params = new HashMap<>();
    params.put("path", "/etc/passwd");
    params.put("text", "hello world");

    TelemetryReporter.sanitize(params);

    assertEquals("<path>", params.get("path"));
    assertEquals("hello world", params.get("text"));
  }

  @Test
  public void testShouldSkipNullValuesWithoutThrowing() {
    Map<String, String> params = new HashMap<>();
    params.put("error", null);
    params.put("path", "/etc/passwd");

    TelemetryReporter.sanitize(params);

    assertNull(params.get("error"));
    assertEquals("<path>", params.get("path"));
  }
}
