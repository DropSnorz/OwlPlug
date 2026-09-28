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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
  public void doesNotDescendIntoPrunedDirectory(@TempDir Path tempDir) throws IOException {
    Path logicBundle = Files.createDirectories(tempDir.resolve("Session.logicx"));
    Path mediaFile = Files.createFile(Files.createDirectories(logicBundle.resolve("Media"))
        .resolve("Audio.wav"));
    Path otherDirectory = Files.createDirectories(tempDir.resolve("Other"));
    Path otherFile = Files.createFile(otherDirectory.resolve("project.rpp"));

    Collection<File> files = FileUtils.listUniqueFilesAndDirs(tempDir.toFile(),
        directory -> !directory.getName().endsWith(".logicx"));

    assertTrue(files.contains(logicBundle.toFile()));
    assertFalse(files.contains(mediaFile.toFile()));
    assertTrue(files.contains(otherFile.toFile()));
  }

}
