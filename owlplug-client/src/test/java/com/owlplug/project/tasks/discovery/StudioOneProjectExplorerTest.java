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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.project.model.DawApplication;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.tasks.discovery.studioone.StudioOneProjectExplorer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StudioOneProjectExplorerTest {

  @Test
  public void studioone7schema8VstValidProject() throws ProjectExplorerException {
    StudioOneProjectExplorer explorer = new StudioOneProjectExplorer();

    File file = new File(this.getClass().getClassLoader()
                             .getResource("projects/studioone/studioone7schema8.song").getFile());

    DawProject project = explorer.explore(file);
    assertEquals("OwlPlug", project.getName());
    assertEquals(DawApplication.STUDIO_ONE, project.getApplication());
    assertEquals("Studio One/7.2.3.108761", project.getAppFullName());
    assertEquals("8", project.getFormatVersion());
    assertEquals(3, project.getPlugins().size());

    assertThat(project.getPlugins(), containsInAnyOrder(
        allOf(
            hasProperty("name", is("Wobbleizer")),
            hasProperty("format", is(PluginFormat.VST2))
        ),
        allOf(
            hasProperty("name", is("iZotope Ozone 5")),
            hasProperty("format", is(PluginFormat.VST2))
        ),
        allOf(
            hasProperty("name", is("Sylenth1")),
            hasProperty("format", is(PluginFormat.VST2))
        )
    ));
  }

  @Test
  public void canExploreAutosavedFile(@TempDir Path tempDir) throws IOException {
    StudioOneProjectExplorer explorer = new StudioOneProjectExplorer();
    File file = Files.createFile(tempDir.resolve("Song (Autosaved).song")).toFile();
    assertTrue(explorer.canExploreFile(file));
  }

  @Test
  public void canExploreFileInHistoryFolder(@TempDir Path tempDir) throws IOException {
    StudioOneProjectExplorer explorer = new StudioOneProjectExplorer();
    Path historyDir = Files.createDirectory(tempDir.resolve("History"));
    File file = Files.createFile(historyDir.resolve("Song.song")).toFile();
    assertTrue(explorer.canExploreFile(file));
  }

  @Test
  public void autosavedFileIsBackupFile() {
    StudioOneProjectExplorer explorer = new StudioOneProjectExplorer();
    File file = new File("/projects/Song (Autosaved).song");
    assertTrue(explorer.isBackupFile(file));
  }

  @Test
  public void fileInHistoryFolderIsBackupFile() {
    StudioOneProjectExplorer explorer = new StudioOneProjectExplorer();
    File file = new File("/projects/History/Song.song");
    assertTrue(explorer.isBackupFile(file));
  }

  @Test
  public void regularSongFileIsNotBackupFile() {
    StudioOneProjectExplorer explorer = new StudioOneProjectExplorer();
    File file = new File(this.getClass().getClassLoader()
                             .getResource("projects/studioone/studioone7schema8.song").getFile());
    assertFalse(explorer.isBackupFile(file));
  }
}
