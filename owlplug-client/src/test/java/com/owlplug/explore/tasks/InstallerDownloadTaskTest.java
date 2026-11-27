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

package com.owlplug.explore.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class InstallerDownloadTaskTest {

  @Test
  public void testFileNameShouldKeepInstallerExtension() {
    assertEquals("Plugin-1.0-win.exe",
        InstallerDownloadTask.fileNameFromUrl("https://example.com/releases/Plugin-1.0-win.exe"));
    assertEquals("Plugin.pkg",
        InstallerDownloadTask.fileNameFromUrl("https://example.com/Plugin.pkg?token=abc#frag"));
    assertEquals("My Plugin.msi",
        InstallerDownloadTask.fileNameFromUrl("https://example.com/My%20Plugin.msi"));
  }

  @Test
  public void testFileNameShouldFallbackWhenUrlHasNoUsableName() {
    assertEquals("installer.bin", InstallerDownloadTask.fileNameFromUrl("https://example.com/download"));
    assertEquals("installer.bin", InstallerDownloadTask.fileNameFromUrl("https://example.com/"));
    assertEquals("installer.bin", InstallerDownloadTask.fileNameFromUrl("https://example.com/.hidden"));
    assertEquals("installer.bin", InstallerDownloadTask.fileNameFromUrl("not a url"));
    assertEquals("installer.bin", InstallerDownloadTask.fileNameFromUrl(null));
  }

  @Test
  public void testFileNameShouldStripPathTraversalAndIllegalCharacters() {
    // Encoded separators are decoded by URI#getPath, only the last segment is kept
    assertEquals("evil.exe", InstallerDownloadTask.fileNameFromUrl("https://example.com/a/..%2F..%2Fevil.exe"));
    assertEquals("a_b.exe", InstallerDownloadTask.fileNameFromUrl("https://example.com/a%3Ab.exe"));
  }

  @Test
  public void testFileNameShouldStripShellMetacharacters() {
    // Decoded names are passed to "cmd /c start" on Windows: no variable expansion or chaining
    assertEquals("_TEMP_.exe", InstallerDownloadTask.fileNameFromUrl("https://example.com/%25TEMP%25.exe"));
    assertEquals("a_calc.exe", InstallerDownloadTask.fileNameFromUrl("https://example.com/a%26calc.exe"));
    assertEquals("a_b_c.msi", InstallerDownloadTask.fileNameFromUrl("https://example.com/a%5Eb%21c.msi"));
  }
}
