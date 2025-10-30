package com.owlplug.core.utils;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


public class StringUtilsTest {

  @Test
  void testTruncate_NormalCase() {
    String result = StringUtils.truncate("HelloWorld", 5, "...");
    assertEquals("He...", result);
  }

  @Test
  void testTruncate_NoTruncationNeeded() {
    String result = StringUtils.truncate("Hi", 5, "...");
    assertEquals("Hi", result);
  }

  @Test
  void testTruncate_NullInput() {
    String result = StringUtils.truncate(null, 5, "...");
    assertEquals("", result);
  }

  @Test
  void testTruncate_NegativeSize() {
    String result = StringUtils.truncate("Hello", -1, "...");
    assertEquals("", result);
  }

  @Test
  void testTruncate_SuffixLongerThanSize() {
    String result = StringUtils.truncate("HelloWorld", 2, "...");
    // max(0, 2 - 3) = 0 → only suffix remains
    assertEquals("...", result);
  }

  // ---------------- ellipsis() ----------------

  @Test
  void testEllipsis_NormalCase() {
    String result = StringUtils.ellipsis("HelloWorld", 7, 2);
    // "HelloWorld" → first (7 - 2)=5 chars "Hello" + "..." + last 2 chars "ld"
    assertEquals("Hello...ld", result);
  }

  @Test
  void testEllipsis_ShortString_NoChange() {
    String result = StringUtils.ellipsis("Hi", 5, 2);
    assertEquals("Hi", result);
  }

  @Test
  void testEllipsis_ClearEndTooLarge() {
    String result = StringUtils.ellipsis("HelloWorld", 5, 6);
    assertEquals("HelloWorld", result);
  }

  @Test
  void testEllipsis_NullInput() {
    String result = StringUtils.ellipsis(null, 10, 2);
    assertNull(result);
  }

  // ---------------- getStackTraceAsString() ----------------

  @Test
  void testGetStackTraceAsString_ContainsExceptionMessage() {
    Exception e = new Exception("Something went wrong");
    String stackTrace = StringUtils.getStackTraceAsString(e);

    assertNotNull(stackTrace);
    assertTrue(stackTrace.contains("Something went wrong"));
    assertTrue(stackTrace.contains("Exception"));
  }

  @Test
  void testGetStackTraceAsString_HandlesCustomThrowable() {
    Throwable t = new Throwable("Custom throwable");
    String stackTrace = StringUtils.getStackTraceAsString(t);

    assertTrue(stackTrace.startsWith("java.lang.Throwable"));
    assertTrue(stackTrace.contains("Custom throwable"));
  }

  @Test
  void testGetStackTraceAsString_NullInput() {
    String result = StringUtils.getStackTraceAsString(null);
    assertEquals("null throwable", result);
  }
}

