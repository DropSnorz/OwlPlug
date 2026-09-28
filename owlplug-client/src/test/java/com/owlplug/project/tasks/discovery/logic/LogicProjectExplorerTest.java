package com.owlplug.project.tasks.discovery.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.project.model.DawApplication;
import com.owlplug.project.model.DawProject;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LogicProjectExplorerTest {

  @Test
  public void canExploreLogicProjectBundle(@TempDir Path tempDir) throws Exception {
    File bundle = Files.createDirectory(tempDir.resolve("Test.logicx")).toFile();

    assertTrue(new LogicProjectExplorer().canExploreFile(bundle));
    assertFalse(new LogicProjectExplorer().canExploreFile(tempDir.toFile()));
  }

  @Test
  public void extractsActiveAudioUnitFromLogicProject(@TempDir Path tempDir) throws Exception {
    Path bundle = tempDir.resolve("Test.logicx");
    Path alternative = Files.createDirectories(bundle.resolve("Alternatives/000"));
    Path resources = Files.createDirectories(bundle.resolve("Resources"));
    Files.writeString(resources.resolve("ProjectInformation.plist"), projectInformationPlist());
    Files.write(alternative.resolve("ProjectData"), projectDataWithActiveInstrument());

    DawProject project = new LogicProjectExplorer().explore(bundle.toFile());

    assertEquals("Test", project.getName());
    assertEquals(DawApplication.LOGIC, project.getApplication());
    assertEquals("Logic Pro 12.3.1", project.getAppFullName());
    assertNotNull(project.getCreatedAt());
    assertNotNull(project.getLastModifiedAt());
    assertEquals(1, project.getPlugins().size());

    var plugin = project.getPlugins().iterator().next();
    assertEquals("Kontakt 8", plugin.getName());
    assertEquals("aumu/NiK8/-NI-", plugin.getUid());
    assertEquals(PluginFormat.AU, plugin.getFormat());
    assertEquals(project, plugin.getProject());
  }

  private byte[] projectDataWithActiveInstrument() {
    byte[] data = new byte[52];
    data[0] = 0;
    data[1] = 0x20;
    byte[] trackName = "Inst 1".getBytes(StandardCharsets.US_ASCII);
    System.arraycopy(trackName, 0, data, 2, trackName.length);

    int descriptorOffset = 17;
    data[descriptorOffset] = 0x29;
    data[descriptorOffset + 1] = (byte) 0xf5;
    data[descriptorOffset + 2] = (byte) 0xf7;
    data[descriptorOffset + 3] = (byte) 0xcf;

    byte[] pluginName = "Kontakt 8".getBytes(StandardCharsets.US_ASCII);
    System.arraycopy(pluginName, 0, data, 27, pluginName.length);
    writeReversedFourCc(data, 40, "-NI-");
    writeReversedFourCc(data, 44, "aumu");
    writeReversedFourCc(data, 48, "NiK8");
    return data;
  }

  private String projectInformationPlist() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<plist version=\"1.0\"><dict>"
        + "<key>LastSavedFrom</key><string>Logic Pro 12.3.1</string>"
        + "</dict></plist>";
  }

  private void writeReversedFourCc(byte[] data, int offset, String value) {
    byte[] bytes = value.getBytes(StandardCharsets.US_ASCII);
    for (int index = 0; index < bytes.length; index++) {
      data[offset + index] = bytes[bytes.length - index - 1];
    }
  }
}