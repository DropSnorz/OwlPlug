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

package com.owlplug.core.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.core.model.OperatingSystem;
import com.owlplug.core.model.RuntimePlatform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RuntimePlatformResolverTest {

  private RuntimePlatformResolver resolver;

  @BeforeEach
  public void setUp() {
    resolver = new RuntimePlatformResolver();
  }

  @Test
  public void testGetCurrentPlatform() {
    RuntimePlatform platform = resolver.getCurrentPlatform();
    assertNotNull(platform);
    assertNotNull(platform.getTag());
    assertNotNull(platform.getOperatingSystem());
    assertNotNull(platform.getArch());
  }

  @Test
  public void testResolve() {
    RuntimePlatform platform = resolver.resolve();
    assertNotNull(platform);
    assertNotNull(platform.getOperatingSystem());
  }

  @Test
  public void testCurrentPlatformHasValidOS() {
    RuntimePlatform platform = resolver.getCurrentPlatform();
    OperatingSystem os = platform.getOperatingSystem();

    // Should be one of the known OS types
    assertTrue(
        os == OperatingSystem.WIN ||
        os == OperatingSystem.MAC ||
        os == OperatingSystem.LINUX ||
        os == OperatingSystem.UNKNOWN
    );
  }

  @Test
  public void testCurrentPlatformHasValidArch() {
    RuntimePlatform platform = resolver.getCurrentPlatform();
    String arch = platform.getArch();

    assertNotNull(arch);
    // Should be one of the known architectures
    assertTrue(
        arch.equals("x64") ||
        arch.equals("x32") ||
        arch.equals("arm64") ||
        arch.equals("arm32") ||
        arch.equals("unknown")
    );
  }

  @Test
  public void testResolveIsConsistent() {
    RuntimePlatform platform1 = resolver.resolve();
    RuntimePlatform platform2 = resolver.resolve();

    // Both resolves should produce the same results
    assertEquals(platform1.getOperatingSystem(), platform2.getOperatingSystem());
    assertEquals(platform1.getArch(), platform2.getArch());
  }

  @Test
  public void testPlatformHasCompatiblePlatforms() {
    RuntimePlatform platform = resolver.getCurrentPlatform();
    assertNotNull(platform.getCompatiblePlatforms());
    assertTrue(platform.getCompatiblePlatforms().size() > 0);

    // Platform should be compatible with itself
    assertTrue(platform.getCompatiblePlatforms().contains(platform));
  }

  @Test
  public void testPlatformCompatibilityTags() {
    RuntimePlatform platform = resolver.getCurrentPlatform();
    assertNotNull(platform.getCompatiblePlatformsTags());
    assertTrue(platform.getCompatiblePlatformsTags().size() > 0);

    // Should contain the OS code
    assertTrue(platform.getCompatiblePlatformsTags().contains(platform.getOperatingSystem().getCode()));
  }

  @Test
  public void testPlatformToString() {
    RuntimePlatform platform = resolver.getCurrentPlatform();
    String toString = platform.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("RuntimePlatform"));
    assertTrue(toString.contains(platform.getTag()));
  }
}