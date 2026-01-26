package com.owlplug.core.services;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TelemetryServiceTest {

  private final TelemetryService telemetryService = new TelemetryService();

  @Test
  public void testShouldRedactUnixAbsolutePath() {
    Map<String, String> params = new HashMap<>();
    params.put("key", "Error reading /var/log/app/error.log file");

    telemetryService.sanitize(params);

    assertEquals("Error reading <path> file", params.get("key"));
  }

  @Test
  public void testShouldRedactWindowsAbsolutePath() {
    Map<String, String> params = new HashMap<>();
    params.put("key", "Failed at C:\\\\Users\\\\john\\\\secret.txt");

    telemetryService.sanitize(params);

    assertEquals("Failed at <path>", params.get("key"));
  }

  @Test
  public void testShouldNotRedactClassNamesOrPackages() {
    Map<String, String> params = new HashMap<>();
    params.put("key", "at com.example.service.MyClass.method(MyClass.java:42)");

    telemetryService.sanitize(params);

    assertEquals(
            "at com.example.service.MyClass.method(MyClass.java:42)",
            params.get("key")
    );
  }

  @Test
  public void testShouldTruncateLongValuesAndAppendEllipsis() {
    Map<String, String> params = new HashMap<>();
    params.put("key", "abcdefghijklmnopqrstuvwxyz");

    telemetryService.sanitize(params,20);

    assertEquals("abcdefghijklmnopqrst…", params.get("key"));
  }

  @Test
  public void testShouldHandleMultipleEntriesIndependently() {
    Map<String, String> params = new HashMap<>();
    params.put("path", "/etc/passwd");
    params.put("text", "hello world");

    telemetryService.sanitize(params);

    assertEquals("<path>", params.get("path"));
    assertEquals("hello world", params.get("text"));
  }
}
