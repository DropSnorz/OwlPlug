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

package com.owlplug.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;

public class RuntimePlatformTest {

  @Test
  public void testRuntimePlatformConstructor() {
    RuntimePlatform platform = new RuntimePlatform("linux-x64", OperatingSystem.LINUX, "x64");

    assertEquals("linux-x64", platform.getTag());
    assertEquals(OperatingSystem.LINUX, platform.getOperatingSystem());
    assertEquals("x64", platform.getArch());
  }

  @Test
  public void testRuntimePlatformConstructorWithAliases() {
    String[] aliases = {"linux64", "amd64-linux"};
    RuntimePlatform platform = new RuntimePlatform("linux-x64", OperatingSystem.LINUX, "x64", aliases);

    assertEquals("linux-x64", platform.getTag());
    Set<String> compatibleTags = platform.getCompatiblePlatformsTags();
    assertTrue(compatibleTags.contains("linux64"));
    assertTrue(compatibleTags.contains("amd64-linux"));
  }

  @Test
  public void testGetCompatiblePlatformsTags() {
    RuntimePlatform platform = new RuntimePlatform("win-x64", OperatingSystem.WIN, "x64");

    Set<String> tags = platform.getCompatiblePlatformsTags();

    assertNotNull(tags);
    assertTrue(tags.contains("win"));
    assertTrue(tags.contains("win-x64"));
  }

  @Test
  public void testGetCompatiblePlatformsTagsWithAliases() {
    String[] aliases = {"win64", "windows-x64"};
    RuntimePlatform platform = new RuntimePlatform("win-x64", OperatingSystem.WIN, "x64", aliases);

    Set<String> tags = platform.getCompatiblePlatformsTags();

    assertTrue(tags.contains("win"));
    assertTrue(tags.contains("win-x64"));
    assertTrue(tags.contains("win64"));
    assertTrue(tags.contains("windows-x64"));
  }

  @Test
  public void testCompatiblePlatformsIncludesSelf() {
    RuntimePlatform platform = new RuntimePlatform("mac-x64", OperatingSystem.MAC, "x64");

    assertTrue(platform.getCompatiblePlatforms().contains(platform));
  }

  @Test
  public void testAddCompatiblePlatform() {
    RuntimePlatform platform64 = new RuntimePlatform("linux-x64", OperatingSystem.LINUX, "x64");
    RuntimePlatform platform32 = new RuntimePlatform("linux-x32", OperatingSystem.LINUX, "x32");

    platform64.getCompatiblePlatforms().add(platform32);

    assertTrue(platform64.getCompatiblePlatforms().contains(platform32));
    Set<String> tags = platform64.getCompatiblePlatformsTags();
    assertTrue(tags.contains("linux-x32"));
  }

  @Test
  public void testSettersAndGetters() {
    RuntimePlatform platform = new RuntimePlatform("test", OperatingSystem.UNKNOWN, "unknown");

    platform.setTag("new-tag");
    platform.setOperatingSystem(OperatingSystem.WIN);
    platform.setArch("x64");

    assertEquals("new-tag", platform.getTag());
    assertEquals(OperatingSystem.WIN, platform.getOperatingSystem());
    assertEquals("x64", platform.getArch());
  }

  @Test
  public void testToString() {
    RuntimePlatform platform = new RuntimePlatform("linux-arm64", OperatingSystem.LINUX, "arm64");

    String str = platform.toString();

    assertNotNull(str);
    assertTrue(str.contains("RuntimePlatform"));
    assertTrue(str.contains("linux-arm64"));
    assertTrue(str.contains("LINUX"));
    assertTrue(str.contains("arm64"));
  }

  @Test
  public void testEmptyAliasesArray() {
    String[] emptyAliases = {};
    RuntimePlatform platform = new RuntimePlatform("mac-arm64", OperatingSystem.MAC, "arm64", emptyAliases);

    Set<String> tags = platform.getCompatiblePlatformsTags();
    assertTrue(tags.contains("mac"));
    assertTrue(tags.contains("mac-arm64"));
  }

  @Test
  public void testMultipleCompatiblePlatforms() {
    RuntimePlatform platform64 = new RuntimePlatform("win-x64", OperatingSystem.WIN, "x64");
    RuntimePlatform platform32 = new RuntimePlatform("win-x32", OperatingSystem.WIN, "x32");
    RuntimePlatform platform16 = new RuntimePlatform("win-x16", OperatingSystem.WIN, "x16");

    platform64.getCompatiblePlatforms().add(platform32);
    platform64.getCompatiblePlatforms().add(platform16);

    Set<String> tags = platform64.getCompatiblePlatformsTags();
    assertTrue(tags.size() >= 4); // win, win-x64, win-x32, win-x16
  }

  @Test
  public void testOperatingSystemCode() {
    RuntimePlatform platform = new RuntimePlatform("linux-x64", OperatingSystem.LINUX, "x64");

    Set<String> tags = platform.getCompatiblePlatformsTags();
    assertTrue(tags.contains(OperatingSystem.LINUX.getCode()));
  }
}