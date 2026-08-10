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

package com.owlplug.core.utils;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


public class StringUtilsTest {

  @Test
  void testTruncateNormalCase() {
    String result = StringUtils.truncate("HelloWorld", 5, "...");
    assertEquals("He...", result);
  }

  @Test
  void testTruncateNoTruncationNeeded() {
    String result = StringUtils.truncate("Hi", 5, "...");
    assertEquals("Hi", result);
  }

  @Test
  void testTruncateNullInput() {
    String result = StringUtils.truncate(null, 5, "...");
    assertEquals("", result);
  }

  @Test
  void testTruncateNegativeSize() {
    String result = StringUtils.truncate("Hello", -1, "...");
    assertEquals("", result);
  }

  @Test
  void testTruncateSuffixLongerThanSize() {
    String result = StringUtils.truncate("HelloWorld", 2, "...");
    // max(0, 2 - 3) = 0 → only suffix remains
    assertEquals("...", result);
  }

  // ---------------- ellipsis() ----------------

  @Test
  void testEllipsisNormalCase() {
    String result = StringUtils.ellipsis("HelloWorld", 7, 2);
    // "HelloWorld" → first (7 - 2)=5 chars "Hello" + "..." + last 2 chars "ld"
    assertEquals("Hello...ld", result);
  }

  @Test
  void testEllipsisShortStringNoChange() {
    String result = StringUtils.ellipsis("Hi", 5, 2);
    assertEquals("Hi", result);
  }

  @Test
  void testEllipsisClearEndTooLarge() {
    String result = StringUtils.ellipsis("HelloWorld", 5, 6);
    assertEquals("HelloWorld", result);
  }

  @Test
  void testEllipsisNullInput() {
    String result = StringUtils.ellipsis(null, 10, 2);
    assertNull(result);
  }

  // ---------------- getStackTraceAsString() ----------------

  @Test
  void testGetStackTraceAsStringContainsExceptionMessage() {
    Exception e = new Exception("Something went wrong");
    String stackTrace = StringUtils.getStackTraceAsString(e);

    assertNotNull(stackTrace);
    assertTrue(stackTrace.contains("Something went wrong"));
    assertTrue(stackTrace.contains("Exception"));
  }

  @Test
  void testGetStackTraceAsStringHandlesCustomThrowable() {
    Throwable t = new Throwable("Custom throwable");
    String stackTrace = StringUtils.getStackTraceAsString(t);

    assertTrue(stackTrace.startsWith("java.lang.Throwable"));
    assertTrue(stackTrace.contains("Custom throwable"));
  }

  @Test
  void testGetStackTraceAsStringNullInput() {
    String result = StringUtils.getStackTraceAsString(null);
    assertEquals("null throwable", result);
  }
}

