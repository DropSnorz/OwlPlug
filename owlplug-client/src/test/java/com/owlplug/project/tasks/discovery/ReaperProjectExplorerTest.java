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

package com.owlplug.project.tasks.discovery;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.project.tasks.discovery.reaper.ReaperProjectExplorer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ReaperProjectExplorerTest {

  @Test
  public void canExploreRppFile(@TempDir Path tempDir) throws IOException {
    ReaperProjectExplorer explorer = new ReaperProjectExplorer();
    File file = Files.createFile(tempDir.resolve("project.rpp")).toFile();
    assertTrue(explorer.canExploreFile(file));
  }

  @Test
  public void canExploreRppBakFile(@TempDir Path tempDir) throws IOException {
    ReaperProjectExplorer explorer = new ReaperProjectExplorer();
    File file = Files.createFile(tempDir.resolve("project.rpp-bak")).toFile();
    assertTrue(explorer.canExploreFile(file));
  }

  @Test
  public void canExploreUppercaseRppFile(@TempDir Path tempDir) throws IOException {
    ReaperProjectExplorer explorer = new ReaperProjectExplorer();
    File file = Files.createFile(tempDir.resolve("project.RPP")).toFile();
    assertTrue(explorer.canExploreFile(file));
  }

  @Test
  public void canExploreUppercaseRppBakFile(@TempDir Path tempDir) throws IOException {
    ReaperProjectExplorer explorer = new ReaperProjectExplorer();
    File file = Files.createFile(tempDir.resolve("project.RPP-BAK")).toFile();
    assertTrue(explorer.canExploreFile(file));
  }

  @Test
  public void cannotExploreUnrelatedFile(@TempDir Path tempDir) throws IOException {
    ReaperProjectExplorer explorer = new ReaperProjectExplorer();
    File file = Files.createFile(tempDir.resolve("notes.txt")).toFile();
    assertFalse(explorer.canExploreFile(file));
  }

  @Test
  public void rppBakFileIsBackupFile() {
    ReaperProjectExplorer explorer = new ReaperProjectExplorer();
    File file = new File("/projects/MyProject/MyProject.rpp-bak");
    assertTrue(explorer.isBackupFile(file));
  }

  @Test
  public void fileInBackupsFolderIsBackupFile() {
    ReaperProjectExplorer explorer = new ReaperProjectExplorer();
    File file = new File("/projects/MyProject/Backups/MyProject-001.rpp");
    assertTrue(explorer.isBackupFile(file));
  }

  @Test
  public void regularRppFileIsNotBackupFile() {
    ReaperProjectExplorer explorer = new ReaperProjectExplorer();
    File file = new File("/projects/MyProject/MyProject.rpp");
    assertFalse(explorer.isBackupFile(file));
  }
}
