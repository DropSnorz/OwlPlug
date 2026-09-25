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

import org.junit.jupiter.api.Test;

public class FileUtilsTest {

  @Test
  public void testSanitizeFileNameWithRegularsChars() {
    String sanitizedFileName = FileUtils.sanitizeFileName("File-name0_.test");
    assertEquals("File-name0_.test", sanitizedFileName);
  }

  @Test
  public void testSanitizeFileNameWithExtraSpaces() {
    String sanitizedFileName = FileUtils.sanitizeFileName(" file   name ");
    assertEquals("file name", sanitizedFileName);
  }
  
  @Test
  public void testSanitizeFileNameWithWhitespacesChars() {
    String sanitizedFileName = FileUtils.sanitizeFileName("\tfile\n\nname");
    assertEquals("filename", sanitizedFileName);
  }
  
  @Test
  public void testSanitizeFileNameWithIllegalChars() {
    String sanitizedFileName = FileUtils.sanitizeFileName("fi/len%am[e]");
    assertEquals("filename", sanitizedFileName);
  }

  @Test
  public void testExpandUserHomeWithLeadingTilde() {
    String userHome = System.getProperty("user.home");
    String expandedPath = FileUtils.expandUserHome("~/.vst3");
    assertEquals(userHome + "/.vst3", expandedPath);
  }

  @Test
  public void testExpandUserHomeWithOnlyTilde() {
    String userHome = System.getProperty("user.home");
    String expandedPath = FileUtils.expandUserHome("~");
    assertEquals(userHome, expandedPath);
  }

  @Test
  public void testExpandUserHomeWithoutLeadingTilde() {
    String expandedPath = FileUtils.expandUserHome("/home/user/.vst3");
    assertEquals("/home/user/.vst3", expandedPath);
  }

  @Test
  public void testExpandUserHomeWithTildeNotAtStart() {
    String expandedPath = FileUtils.expandUserHome("/home/user/~backup/.vst3");
    assertEquals("/home/user/~backup/.vst3", expandedPath);
  }

  @Test
  public void testExpandUserHomeWithNullPath() {
    String expandedPath = FileUtils.expandUserHome(null);
    assertEquals(null, expandedPath);
  }

}
