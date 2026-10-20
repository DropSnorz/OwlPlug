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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.owlplug.core.model.OperatingSystem;
import com.owlplug.core.model.RuntimePlatform;
import com.owlplug.plugin.model.PluginFormat;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Note: ApplicationDefaults is mocked with CALLS_REAL_METHODS rather than constructed
 * directly, since its constructor eagerly loads JavaFX Image resources which requires
 * a JavaFX toolkit that isn't available in this plain unit test.
 */
@ExtendWith(MockitoExtension.class)
class ApplicationDefaultsTest {

  private ApplicationDefaults applicationDefaults;
  private RuntimePlatformResolver runtimePlatformResolver;

  @BeforeEach
  void setUp() {
    applicationDefaults = mock(ApplicationDefaults.class, CALLS_REAL_METHODS);
    runtimePlatformResolver = mock(RuntimePlatformResolver.class);
    ReflectionTestUtils.setField(applicationDefaults, "runtimePlatformResolver", runtimePlatformResolver);
  }

  private void mockOperatingSystem(OperatingSystem os) {
    RuntimePlatform platform = mock(RuntimePlatform.class);
    when(platform.getOperatingSystem()).thenReturn(os);
    when(runtimePlatformResolver.getCurrentPlatform()).thenReturn(platform);
  }

  @Test
  void getDefaultPluginPathOnLinuxUsesUserHomeDirectory() {
    mockOperatingSystem(OperatingSystem.LINUX);
    String userHome = System.getProperty("user.home");

    assertEquals(userHome + File.separator + ".vst",
        applicationDefaults.getDefaultPluginPath(PluginFormat.VST2));
    assertEquals(userHome + File.separator + ".vst3",
        applicationDefaults.getDefaultPluginPath(PluginFormat.VST3));
    assertEquals(userHome + File.separator + ".lv2",
        applicationDefaults.getDefaultPluginPath(PluginFormat.LV2));
  }

  @Test
  void getDefaultPluginPathOnLinuxIsNotSystemWideDirectory() {
    mockOperatingSystem(OperatingSystem.LINUX);

    assertTrue(applicationDefaults.getDefaultPluginPath(PluginFormat.VST3)
        .startsWith(System.getProperty("user.home")));
  }

  @Test
  void getLinuxSystemPluginPathReturnsLegacySystemWideDirectory() {
    mockOperatingSystem(OperatingSystem.LINUX);

    assertEquals("/usr/lib/vst", applicationDefaults.getLinuxSystemPluginPath(PluginFormat.VST2));
    assertEquals("/usr/lib/vst3", applicationDefaults.getLinuxSystemPluginPath(PluginFormat.VST3));
    assertEquals("/usr/lib/lv2", applicationDefaults.getLinuxSystemPluginPath(PluginFormat.LV2));
  }

  @Test
  void getLinuxSystemPluginPathReturnsNullOnOtherOperatingSystems() {
    mockOperatingSystem(OperatingSystem.WIN);

    assertNull(applicationDefaults.getLinuxSystemPluginPath(PluginFormat.VST3));
  }

  @Test
  void getDefaultPluginPathOnWindowsUsesProgramFiles() {
    mockOperatingSystem(OperatingSystem.WIN);

    assertEquals("C:/Program Files/Common Files/VST3",
        applicationDefaults.getDefaultPluginPath(PluginFormat.VST3));
  }

  @Test
  void getDefaultPluginPathOnMacUsesLibraryAudioPlugins() {
    mockOperatingSystem(OperatingSystem.MAC);

    assertEquals("/Library/Audio/Plug-ins/VST3",
        applicationDefaults.getDefaultPluginPath(PluginFormat.VST3));
  }

}
