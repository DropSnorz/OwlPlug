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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug. If not, see <https://www.gnu.org/licenses/>.
 */

package com.owlplug.project.tasks.discovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.project.model.DawApplication;
import com.owlplug.project.model.DawPlugin;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.tasks.discovery.dawproject.DawprojectProjectExplorer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DawprojectProjectExplorerTest {

  @TempDir
  Path directory;

  private final DawprojectProjectExplorer explorer = new DawprojectProjectExplorer();

  @Test
  public void readsPluginReferencesAndExporterMetadata() throws Exception {
    String projectXml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <Project version="1.0">
          <Application name="Bitwig Studio" version="5.3.13"/>
          <Structure>
            <Track>
              <Channel>
                <Devices>
                  <Vst2Plugin deviceName="Legacy Synth" deviceID="123456" deviceRole="instrument"/>
                  <Vst3Plugin deviceName="Modern Synth" deviceID="00112233-4455-6677-8899-aabbccddeeff"
                              deviceRole="instrument" loaded="false"/>
                  <ClapPlugin deviceName="Future Plugin" deviceID="org.example.future"
                              deviceRole="audioFX"/>
                </Devices>
              </Channel>
              <Track>
                <Channel>
                  <Devices>
                    <AuPlugin deviceName="Mac Reverb" deviceID="AUMF" deviceRole="audioFX"/>
                  </Devices>
                </Channel>
              </Track>
            </Track>
          </Structure>
        </Project>
        """;
    Path file = projectFile("Song.DAWPROJECT", Map.of(
        "project.xml", projectXml,
        "metadata.xml", "<MetaData><Title>Example Song</Title></MetaData>"
    ));

    DawProject project = explorer.explore(file.toFile());

    assertNotNull(project);
    assertEquals(DawApplication.DAWPROJECT, project.getApplication());
    assertEquals("Example Song", project.getName());
    assertEquals("Bitwig Studio 5.3.13", project.getAppFullName());
    assertEquals("1.0", project.getFormatVersion());
    assertEquals(3, project.getPlugins().size());
    assertPlugin(project, "Legacy Synth", "123456", PluginFormat.VST2);
    assertPlugin(project, "Modern Synth", "00112233-4455-6677-8899-aabbccddeeff", PluginFormat.VST3);
    assertPlugin(project, "Mac Reverb", "AUMF", PluginFormat.AU);
    assertFalse(project.getPlugins().stream().anyMatch(p -> p.getName().equals("Future Plugin")));
    assertNotNull(project.getLastModifiedAt());
    assertNotNull(project.getCreatedAt());
  }

  @Test
  public void usesFilenameWhenMetadataIsMissing() throws Exception {
    Path file = projectFile("Untitled.dawproject", Map.of(
        "project.xml", "<Project version=\"1.0\"><Application name=\"Studio One\" version=\"7\"/></Project>"
    ));
    DawProject project = explorer.explore(file.toFile());
    assertEquals("Untitled", project.getName());
    assertEquals("Studio One 7", project.getAppFullName());
    assertTrue(project.getPlugins().isEmpty());
  }

  @Test
  public void handlesBitwigExportsWithDuplicatePluginInstancesAndEmptyTitle() throws Exception {
    Path file = projectFile("Session.dawproject", Map.of(
        "project.xml", """
            <Project version="1.0">
              <Application name="Bitwig Studio" version="6.1"/>
              <Structure>
                <Track><Channel><Devices>
                  <Vst3Plugin deviceName="Example Synth" deviceID="AABBCCDDEEFF00112233445566778899"
                              deviceRole="instrument" loaded="true"/>
                  <Vst3Plugin deviceName="Example Synth" deviceID="AABBCCDDEEFF00112233445566778899"
                              deviceRole="instrument" loaded="true"/>
                </Devices></Channel></Track>
              </Structure>
            </Project>
            """,
        "metadata.xml", "<MetaData><Title/></MetaData>"
    ));

    DawProject project = explorer.explore(file.toFile());
    assertEquals("Session", project.getName());
    assertEquals("Bitwig Studio 6.1", project.getAppFullName());
    assertEquals(1, project.getPlugins().size());
    assertPlugin(project, "Example Synth", "AABBCCDDEEFF00112233445566778899",
        PluginFormat.VST3);
  }

  @Test
  public void rejectsArchivesWithoutProjectXml() throws Exception {
    Path file = projectFile("Empty.dawproject", Map.of(
        "metadata.xml", "<MetaData><Title>No project</Title></MetaData>"
    ));
    assertThrows(ProjectExplorerException.class, () -> explorer.explore(file.toFile()));
  }

  @Test
  public void rejectsInvalidProjectXml() throws Exception {
    Path file = projectFile("Broken.dawproject", Map.of(
        "project.xml", "<Other version=\"1.0\"/>"
    ));
    assertThrows(ProjectExplorerException.class, () -> explorer.explore(file.toFile()));
  }

  @Test
  public void ignoresOtherExtensions() throws IOException, ProjectExplorerException {
    Path file = Files.createFile(directory.resolve("Song.bwproject"));
    assertFalse(explorer.canExploreFile(file.toFile()));
    assertNull(explorer.explore(file.toFile()));
  }

  @Test
  public void respectsBackupDetection() throws Exception {
    Path backups = Files.createDirectories(directory.resolve("Backups"));
    Path file = backups.resolve("Song.dawproject");
    Files.createFile(file);
    assertTrue(explorer.isBackupFile(file.toFile()));
  }

  private Path projectFile(String filename, Map<String, String> entries) throws IOException {
    Path file = directory.resolve(filename);
    try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(file))) {
      for (Map.Entry<String, String> entry : entries.entrySet()) {
        zip.putNextEntry(new ZipEntry(entry.getKey()));
        zip.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
      }
    }
    return file;
  }

  private void assertPlugin(DawProject project, String name, String uid, PluginFormat format) {
    DawPlugin plugin = project.getPlugins().stream()
        .filter(p -> p.getName().equals(name))
        .findFirst().orElseThrow();
    assertEquals(uid, plugin.getUid());
    assertEquals(format, plugin.getFormat());
    assertEquals(project, plugin.getProject());
  }
}
