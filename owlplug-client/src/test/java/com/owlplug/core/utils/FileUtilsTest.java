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
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assumptions;
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

  private static void link(Path link, Path target) throws IOException {
    try {
      Files.createSymbolicLink(link, target);
    } catch (UnsupportedOperationException | IOException e) {
      Assumptions.abort("Symlinks not supported: " + e.getMessage());
    }
  }

  private static Set<Path> paths(Collection<File> files) {
    return files.stream().map(File::toPath).collect(Collectors.toSet());
  }

  @Test
  public void testListPlainTree(@TempDir Path tmp) throws IOException {
    Path root = Files.createDirectory(tmp.resolve("root"));
    Files.createDirectory(root.resolve("a"));
    Files.createDirectory(root.resolve("b"));
    Files.createFile(root.resolve("a/1.txt"));
    Files.createFile(root.resolve("a/2.txt"));
    Files.createFile(root.resolve("b/3.txt"));

    assertEquals(6, FileUtils.listUniqueFilesAndDirs(root.toFile()).size());
  }

  @Test
  public void testListSelfLink(@TempDir Path tmp) throws IOException {
    Path root = Files.createDirectory(tmp.resolve("root"));
    Files.createFile(root.resolve("a.txt"));
    link(root.resolve("loop"), Path.of("."));

    Set<Path> result = paths(FileUtils.listUniqueFilesAndDirs(root.toFile()));
    assertEquals(3, result.size());
    assertTrue(result.contains(root.resolve("loop")));
    assertFalse(result.contains(root.resolve("loop/a.txt")));
  }

  @Test
  public void testListManyAncestorLinks(@TempDir Path tmp) throws IOException {
    Path root = Files.createDirectory(tmp.resolve("root"));
    for (int i = 0; i < 20; i++) {
      Files.createDirectory(root.resolve("p" + i + ".vst3"));
    }
    int plainCount = FileUtils.listUniqueFilesAndDirs(root.toFile()).size();

    String abs = root.toAbsolutePath().toString();
    link(root.resolve("l0"), Path.of(abs));
    link(root.resolve("l1"), Path.of("."));
    link(root.resolve("l2"), Path.of("../" + root.getFileName()));
    link(root.resolve("l3"), Path.of("./"));
    link(root.resolve("l4"), Path.of(abs + "/"));
    link(root.resolve("l5"), Path.of("././"));

    Collection<File> result = FileUtils.listUniqueFilesAndDirs(root.toFile());
    assertEquals(plainCount + 6, result.size());
    assertEquals(20, result.stream().filter(f -> f.getName().endsWith(".vst3")).count());
  }

  @Test
  public void testListFollowsLinkOutsideRoot(@TempDir Path tmp) throws IOException {
    Path root = Files.createDirectories(tmp.resolve("root/Plugins"));
    Path ext = Files.createDirectories(tmp.resolve("ext"));
    Files.createDirectory(ext.resolve("b.vst3"));
    link(root.resolve("ext"), ext);

    Set<Path> result = paths(FileUtils.listUniqueFilesAndDirs(tmp.resolve("root").toFile()));
    assertTrue(result.contains(root.resolve("ext/b.vst3")));
  }

  @Test
  public void testListCycleThroughOutsideDir(@TempDir Path tmp) throws IOException {
    Path root = Files.createDirectories(tmp.resolve("root/Plugins"));
    Path ext = Files.createDirectories(tmp.resolve("ext"));
    Files.createDirectory(ext.resolve("b.vst3"));
    link(root.resolve("ext"), ext);
    link(ext.resolve("back"), tmp.resolve("root"));

    Set<Path> result = paths(FileUtils.listUniqueFilesAndDirs(tmp.resolve("root").toFile()));
    assertTrue(result.contains(root.resolve("ext/back")));
    assertFalse(result.contains(root.resolve("ext/back/Plugins")));
  }

  @Test
  public void testListPrefersPhysicalPath(@TempDir Path tmp) throws IOException {
    Path root = Files.createDirectory(tmp.resolve("root"));
    Files.createDirectories(root.resolve("A/p.vst3"));
    Files.createDirectory(root.resolve("B"));
    link(root.resolve("B/linkToA"), Path.of("../A"));

    Set<Path> result = paths(FileUtils.listUniqueFilesAndDirs(root.toFile()));
    assertTrue(result.contains(root.resolve("A/p.vst3")));
    assertTrue(result.contains(root.resolve("B/linkToA")));
    assertFalse(result.contains(root.resolve("B/linkToA/p.vst3")));
  }

  @Test
  public void testListBrokenLink(@TempDir Path tmp) throws IOException {
    Path root = Files.createDirectory(tmp.resolve("root"));
    link(root.resolve("broken"), Path.of("/does/not/exist"));

    Collection<File> result = FileUtils.listUniqueFilesAndDirs(root.toFile());
    assertTrue(paths(result).contains(root.resolve("broken")));
  }

  @Test
  public void testListNotADirectory(@TempDir Path tmp) throws IOException {
    Path file = Files.createFile(tmp.resolve("file.txt"));
    assertTrue(FileUtils.listUniqueFilesAndDirs(file.toFile()).isEmpty());
    assertTrue(FileUtils.listUniqueFilesAndDirs(tmp.resolve("missing").toFile()).isEmpty());
  }

}
