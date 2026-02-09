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

package com.owlplug.core.model.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class RemoteVersionTest {

  @Test
  public void testRemoteVersionDefaultConstructor() {
    RemoteVersion remoteVersion = new RemoteVersion();
    assertNotNull(remoteVersion);
    assertNull(remoteVersion.version);
  }

  @Test
  public void testRemoteVersionSetVersion() {
    RemoteVersion remoteVersion = new RemoteVersion();
    remoteVersion.version = "1.2.3";
    assertEquals("1.2.3", remoteVersion.version);
  }

  @Test
  public void testRemoteVersionDeserializationFromJson() throws Exception {
    String json = "{\"version\":\"2.0.0\"}";
    ObjectMapper mapper = new ObjectMapper();

    RemoteVersion remoteVersion = mapper.readValue(json, RemoteVersion.class);

    assertNotNull(remoteVersion);
    assertEquals("2.0.0", remoteVersion.version);
  }

  @Test
  public void testRemoteVersionDeserializationWithExtraFields() throws Exception {
    // JSON with extra fields should be ignored due to @JsonIgnoreProperties
    String json = "{\"version\":\"3.1.4\",\"extraField\":\"ignored\",\"anotherField\":123}";
    ObjectMapper mapper = new ObjectMapper();

    RemoteVersion remoteVersion = mapper.readValue(json, RemoteVersion.class);

    assertNotNull(remoteVersion);
    assertEquals("3.1.4", remoteVersion.version);
  }

  @Test
  public void testRemoteVersionDeserializationEmptyJson() throws Exception {
    String json = "{}";
    ObjectMapper mapper = new ObjectMapper();

    RemoteVersion remoteVersion = mapper.readValue(json, RemoteVersion.class);

    assertNotNull(remoteVersion);
    assertNull(remoteVersion.version);
  }

  @Test
  public void testRemoteVersionDeserializationNullVersion() throws Exception {
    String json = "{\"version\":null}";
    ObjectMapper mapper = new ObjectMapper();

    RemoteVersion remoteVersion = mapper.readValue(json, RemoteVersion.class);

    assertNotNull(remoteVersion);
    assertNull(remoteVersion.version);
  }

  @Test
  public void testRemoteVersionSerializationToJson() throws Exception {
    RemoteVersion remoteVersion = new RemoteVersion();
    remoteVersion.version = "4.5.6";

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(remoteVersion);

    assertNotNull(json);
    assertEquals("{\"version\":\"4.5.6\"}", json);
  }

  @Test
  public void testRemoteVersionSerializationNullVersion() throws Exception {
    RemoteVersion remoteVersion = new RemoteVersion();

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(remoteVersion);

    assertNotNull(json);
    assertEquals("{\"version\":null}", json);
  }

  @Test
  public void testRemoteVersionRoundTrip() throws Exception {
    RemoteVersion original = new RemoteVersion();
    original.version = "5.0.0-beta";

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(original);
    RemoteVersion deserialized = mapper.readValue(json, RemoteVersion.class);

    assertEquals(original.version, deserialized.version);
  }

  @Test
  public void testRemoteVersionWithSemanticVersion() {
    RemoteVersion remoteVersion = new RemoteVersion();
    remoteVersion.version = "1.0.0-alpha.1+build.123";
    assertEquals("1.0.0-alpha.1+build.123", remoteVersion.version);
  }

  @Test
  public void testRemoteVersionWithSimpleVersion() {
    RemoteVersion remoteVersion = new RemoteVersion();
    remoteVersion.version = "2";
    assertEquals("2", remoteVersion.version);
  }

  @Test
  public void testRemoteVersionWithComplexVersion() {
    RemoteVersion remoteVersion = new RemoteVersion();
    remoteVersion.version = "10.20.30-rc.1+sha.5114f85";
    assertEquals("10.20.30-rc.1+sha.5114f85", remoteVersion.version);
  }
}