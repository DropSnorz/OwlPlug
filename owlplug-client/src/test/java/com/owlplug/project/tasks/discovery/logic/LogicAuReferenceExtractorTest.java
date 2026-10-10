package com.owlplug.project.tasks.discovery.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LogicAuReferenceExtractorTest {

  @Test
  public void extractsAudioUnitFingerprintFromLittleEndianFourCc() {
    byte[] projectData = new byte[12];
    writeReversedFourCc(projectData, 0, "NI--");
    writeReversedFourCc(projectData, 4, "aumu");
    writeReversedFourCc(projectData, 8, "NiK8");

    List<LogicAuReference> references = new LogicAuReferenceExtractor().extract(projectData);

    assertEquals(1, references.size());
    assertEquals("aumu", references.get(0).typeCode());
    assertEquals("NiK8", references.get(0).subtype());
    assertEquals("NI--", references.get(0).manufacturer());
    assertEquals("aumu/NiK8/NI--", references.get(0).fingerprint());
    assertEquals(4, references.get(0).offset());
  }

  @Test
  public void extractsPluginDisplayNameFromNearbyBinaryString() {
    byte[] projectData = new byte[44];
    byte[] nameBytes = "Plugin Name".getBytes(StandardCharsets.US_ASCII);
    System.arraycopy(nameBytes, 0, projectData, 0, nameBytes.length);
    writeReference(projectData, 32, "aufx", "Fx01", "Mfr1");

    List<LogicAuReference> references = new LogicAuReferenceExtractor().extract(projectData);

    assertEquals("Plugin Name", references.get(0).displayName());
  }

  @Test
  public void extractsInstrumentMidiEffectAndAudioEffectTypes() {
    byte[] projectData = new byte[36];
    writeReference(projectData, 0, "aumu", "Inst", "Mfr1");
    writeReference(projectData, 12, "aumf", "Midi", "Mfr2");
    writeReference(projectData, 24, "aufx", "Fx01", "Mfr3");

    List<LogicAuReference> references = new LogicAuReferenceExtractor().extract(projectData);

    assertEquals(List.of("aumu", "aumf", "aufx"),
        references.stream().map(LogicAuReference::typeCode).toList());
  }

  @Test
  public void ignoresUnsupportedTypesAndNonPrintableIdentifiers() {
    byte[] projectData = new byte[24];
    writeReference(projectData, 0, "vst3", "Plug", "Mfr1");
    projectData[12] = 0x01;
    writeReversedFourCc(projectData, 16, "aumu");
    writeReversedFourCc(projectData, 20, "Plug");

    assertTrue(new LogicAuReferenceExtractor().extract(projectData).isEmpty());
  }

  @Test
  public void ignoresReferencesOnInactiveTracks() {
    byte[] projectData = new byte[108];
    writeTrackStrip(projectData, 1, "Inst 1", true);
    writeReference(projectData, 36, "aumu", "Plug", "Mfr1");
    writeTrackStrip(projectData, 60, "Inst 2", false);
    writeReference(projectData, 96, "aumu", "Plug", "Mfr2");

    List<LogicAuReference> references = new LogicAuReferenceExtractor()
        .extractFromActiveUserTracks(projectData);

    assertEquals(1, references.size());
    assertEquals("aumu/Plug/Mfr1", references.get(0).fingerprint());
  }

  private void writeReference(byte[] data, int offset, String type, String subtype, String manufacturer) {
    writeReversedFourCc(data, offset, manufacturer);
    writeReversedFourCc(data, offset + 4, type);
    writeReversedFourCc(data, offset + 8, subtype);
  }

  private void writeTrackStrip(byte[] data, int markerOffset, String name, boolean active) {
    data[markerOffset - 1] = 0;
    data[markerOffset] = 0x20;
    byte[] nameBytes = name.getBytes(StandardCharsets.US_ASCII);
    System.arraycopy(nameBytes, 0, data, markerOffset + 1, nameBytes.length);

    int descriptorOffset = markerOffset + 16;
    data[descriptorOffset] = 0x29;
    data[descriptorOffset + 1] = (byte) 0xf5;
    data[descriptorOffset + 2] = (byte) (active ? 0xf7 : 0xf3);
    data[descriptorOffset + 3] = (byte) 0xcf;
  }

  private void writeReversedFourCc(byte[] data, int offset, String value) {
    byte[] bytes = value.getBytes(StandardCharsets.US_ASCII);
    for (int index = 0; index < bytes.length; index++) {
      data[offset + index] = bytes[bytes.length - index - 1];
    }
  }
}