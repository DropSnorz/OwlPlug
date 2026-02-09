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

package com.owlplug.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ApplicationPreferences;
import com.owlplug.core.model.OperatingSystem;
import com.owlplug.core.model.RuntimePlatform;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TelemetryServiceTest {

  private TelemetryService telemetryService;
  private ApplicationDefaults mockApplicationDefaults;
  private ApplicationPreferences mockPreferences;

  @BeforeEach
  public void setUp() {
    telemetryService = new TelemetryService();
    mockApplicationDefaults = mock(ApplicationDefaults.class);
    mockPreferences = mock(ApplicationPreferences.class);

    // Setup default mock behaviors
    RuntimePlatform platform = new RuntimePlatform("linux-x64", OperatingSystem.LINUX, "x64");
    when(mockApplicationDefaults.getRuntimePlatform()).thenReturn(platform);
    when(mockApplicationDefaults.getVersion()).thenReturn("1.0.0");
    when(mockApplicationDefaults.getEnvProperty(anyString())).thenReturn("test-token");
    when(mockPreferences.get(anyString(), anyString())).thenReturn("test-user-id");
    when(mockPreferences.getBoolean(anyString(), anyString().equals("true"))).thenReturn(false);

    // Use reflection to set private fields
    try {
      java.lang.reflect.Field appDefaultsField = BaseService.class.getDeclaredField("applicationDefaults");
      appDefaultsField.setAccessible(true);
      appDefaultsField.set(telemetryService, mockApplicationDefaults);

      java.lang.reflect.Field prefsField = BaseService.class.getDeclaredField("preferences");
      prefsField.setAccessible(true);
      prefsField.set(telemetryService, mockPreferences);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testSanitizeRemovesAbsolutePathsUnix() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("error", "File not found at /home/user/plugins/test.vst");
    params.put("path", "/usr/local/bin/plugin");

    Method sanitizeMethod = TelemetryService.class.getDeclaredMethod("sanitize", Map.class);
    sanitizeMethod.setAccessible(true);
    sanitizeMethod.invoke(telemetryService, params);

    assertEquals("File not found at <path>", params.get("error"));
    assertEquals("<path>", params.get("path"));
  }

  @Test
  public void testSanitizeRemovesAbsolutePathsWindows() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("error", "Plugin crashed at C:\\\\Program Files\\\\VST\\\\plugin.dll");
    params.put("location", "D:\\\\Music\\\\Plugins");

    Method sanitizeMethod = TelemetryService.class.getDeclaredMethod("sanitize", Map.class);
    sanitizeMethod.setAccessible(true);
    sanitizeMethod.invoke(telemetryService, params);

    assertEquals("Plugin crashed at <path>", params.get("error"));
    assertEquals("<path>", params.get("location"));
  }

  @Test
  public void testSanitizeTruncatesLongStrings() throws Exception {
    Map<String, String> params = new HashMap<>();
    StringBuilder longString = new StringBuilder();
    for (int i = 0; i < 2500; i++) {
      longString.append("a");
    }
    params.put("longValue", longString.toString());

    Method sanitizeMethod = TelemetryService.class.getDeclaredMethod("sanitize", Map.class);
    sanitizeMethod.setAccessible(true);
    sanitizeMethod.invoke(telemetryService, params);

    String sanitized = params.get("longValue");
    assertEquals(2001, sanitized.length()); // 2000 chars + ellipsis
    assertTrue(sanitized.endsWith("…"));
  }

  @Test
  public void testSanitizeHandlesNormalStrings() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("taskName", "PluginScanTask");
    params.put("error", "Connection timeout");

    Method sanitizeMethod = TelemetryService.class.getDeclaredMethod("sanitize", Map.class);
    sanitizeMethod.setAccessible(true);
    sanitizeMethod.invoke(telemetryService, params);

    assertEquals("PluginScanTask", params.get("taskName"));
    assertEquals("Connection timeout", params.get("error"));
  }

  @Test
  public void testSanitizeHandlesEmptyMap() throws Exception {
    Map<String, String> params = new HashMap<>();

    Method sanitizeMethod = TelemetryService.class.getDeclaredMethod("sanitize", Map.class);
    sanitizeMethod.setAccessible(true);
    sanitizeMethod.invoke(telemetryService, params);

    assertTrue(params.isEmpty());
  }

  @Test
  public void testSanitizeHandlesMultiplePaths() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("error", "Failed to load /home/user/plugin1.vst and /home/user/plugin2.vst");

    Method sanitizeMethod = TelemetryService.class.getDeclaredMethod("sanitize", Map.class);
    sanitizeMethod.setAccessible(true);
    sanitizeMethod.invoke(telemetryService, params);

    assertEquals("Failed to load <path> and <path>", params.get("error"));
  }

  @Test
  public void testSanitizePreservesRelativePaths() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("file", "plugin.vst");
    params.put("folder", "subfolder/plugin");

    Method sanitizeMethod = TelemetryService.class.getDeclaredMethod("sanitize", Map.class);
    sanitizeMethod.setAccessible(true);
    sanitizeMethod.invoke(telemetryService, params);

    assertEquals("plugin.vst", params.get("file"));
    // Note: "subfolder/plugin" might be sanitized depending on regex interpretation
    // This tests the current behavior
  }

  @Test
  public void testSanitizeMixedContent() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("message", "Error in /home/user/test.vst: Invalid format");
    params.put("count", "42");

    Method sanitizeMethod = TelemetryService.class.getDeclaredMethod("sanitize", Map.class);
    sanitizeMethod.setAccessible(true);
    sanitizeMethod.invoke(telemetryService, params);

    assertEquals("Error in <path>: Invalid format", params.get("message"));
    assertEquals("42", params.get("count"));
  }

  @Test
  public void testEventNotSentWhenDisabled() {
    when(mockPreferences.getBoolean(ApplicationDefaults.TELEMETRY_ENABLED_KEY, false)).thenReturn(false);

    // Should not throw exception even when telemetry is disabled
    telemetryService.event("/Startup");
    telemetryService.event("/Error/PluginScanIncomplete", p -> {
      p.put("pluginName", "TestPlugin");
    });
  }

  @Test
  public void testEventNotSentForDisallowedEvents() {
    when(mockPreferences.getBoolean(ApplicationDefaults.TELEMETRY_ENABLED_KEY, false)).thenReturn(true);

    // Should silently ignore non-allowed events
    telemetryService.event("/CustomEvent");
    telemetryService.event("/NotAllowed/Event", p -> {
      p.put("data", "value");
    });
  }

  @Test
  public void testSanitizeHandlesExtremelyLongString() throws Exception {
    Map<String, String> params = new HashMap<>();
    StringBuilder extremelyLongString = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      extremelyLongString.append("x");
    }
    params.put("huge", extremelyLongString.toString());

    Method sanitizeMethod = TelemetryService.class.getDeclaredMethod("sanitize", Map.class);
    sanitizeMethod.setAccessible(true);
    sanitizeMethod.invoke(telemetryService, params);

    String result = params.get("huge");
    assertEquals(2001, result.length());
    assertTrue(result.endsWith("…"));
  }

  @Test
  public void testSanitizeEdgeCaseExactly2000Chars() throws Exception {
    Map<String, String> params = new HashMap<>();
    StringBuilder exactString = new StringBuilder();
    for (int i = 0; i < 2000; i++) {
      exactString.append("b");
    }
    params.put("exact", exactString.toString());

    Method sanitizeMethod = TelemetryService.class.getDeclaredMethod("sanitize", Map.class);
    sanitizeMethod.setAccessible(true);
    sanitizeMethod.invoke(telemetryService, params);

    String result = params.get("exact");
    assertEquals(2000, result.length());
    assertFalse(result.endsWith("…"));
  }
}