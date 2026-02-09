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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.owlplug.core.model.OperatingSystem;
import com.owlplug.core.model.RuntimePlatform;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.model.PluginType;
import com.owlplug.project.model.DawApplication;
import java.util.List;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

public class ApplicationDefaultsTest {

  private ApplicationDefaults applicationDefaults;
  private RuntimePlatformResolver mockResolver;
  private Environment mockEnv;

  @BeforeEach
  public void setUp() {
    applicationDefaults = new ApplicationDefaults();
    mockResolver = mock(RuntimePlatformResolver.class);
    mockEnv = mock(Environment.class);

    // Use reflection to set private fields
    try {
      java.lang.reflect.Field resolverField = ApplicationDefaults.class.getDeclaredField("runtimePlatformResolver");
      resolverField.setAccessible(true);
      resolverField.set(applicationDefaults, mockResolver);

      java.lang.reflect.Field envField = ApplicationDefaults.class.getDeclaredField("env");
      envField.setAccessible(true);
      envField.set(applicationDefaults, mockEnv);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testGetPluginFormatIconVST2() {
    Image icon = applicationDefaults.getPluginFormatIcon(PluginFormat.VST2);
    assertNotNull(icon);
  }

  @Test
  public void testGetPluginFormatIconVST3() {
    Image icon = applicationDefaults.getPluginFormatIcon(PluginFormat.VST3);
    assertNotNull(icon);
  }

  @Test
  public void testGetPluginFormatIconAU() {
    Image icon = applicationDefaults.getPluginFormatIcon(PluginFormat.AU);
    assertNotNull(icon);
  }

  @Test
  public void testGetPluginFormatIconLV2() {
    Image icon = applicationDefaults.getPluginFormatIcon(PluginFormat.LV2);
    assertNotNull(icon);
  }

  @Test
  public void testGetPackageTypeIconInstrument() {
    RemotePackage remotePackage = new RemotePackage();
    remotePackage.setType(PluginType.INSTRUMENT);

    Image icon = applicationDefaults.getPackageTypeIcon(remotePackage);
    assertNotNull(icon);
  }

  @Test
  public void testGetPackageTypeIconEffect() {
    RemotePackage remotePackage = new RemotePackage();
    remotePackage.setType(PluginType.EFFECT);

    Image icon = applicationDefaults.getPackageTypeIcon(remotePackage);
    assertNotNull(icon);
  }

  @Test
  public void testGetDAWApplicationIconAbleton() {
    Image icon = applicationDefaults.getDAWApplicationIcon(DawApplication.ABLETON);
    assertNotNull(icon);
  }

  @Test
  public void testGetDAWApplicationIconReaper() {
    Image icon = applicationDefaults.getDAWApplicationIcon(DawApplication.REAPER);
    assertNotNull(icon);
  }

  @Test
  public void testGetDAWApplicationIconStudioOne() {
    Image icon = applicationDefaults.getDAWApplicationIcon(DawApplication.STUDIO_ONE);
    assertNotNull(icon);
  }

  @Test
  public void testGetDefaultPluginPathWindowsVST2() {
    RuntimePlatform platform = new RuntimePlatform("win-x64", OperatingSystem.WIN, "x64");
    when(mockResolver.getCurrentPlatform()).thenReturn(platform);

    String path = applicationDefaults.getDefaultPluginPath(PluginFormat.VST2);
    assertEquals("C:/Program Files/VSTPlugins", path);
  }

  @Test
  public void testGetDefaultPluginPathWindowsVST3() {
    RuntimePlatform platform = new RuntimePlatform("win-x64", OperatingSystem.WIN, "x64");
    when(mockResolver.getCurrentPlatform()).thenReturn(platform);

    String path = applicationDefaults.getDefaultPluginPath(PluginFormat.VST3);
    assertEquals("C:/Program Files/Common Files/VST3", path);
  }

  @Test
  public void testGetDefaultPluginPathWindowsLV2() {
    RuntimePlatform platform = new RuntimePlatform("win-x64", OperatingSystem.WIN, "x64");
    when(mockResolver.getCurrentPlatform()).thenReturn(platform);

    String path = applicationDefaults.getDefaultPluginPath(PluginFormat.LV2);
    assertEquals("C:/Program Files/Common Files/LV2", path);
  }

  @Test
  public void testGetDefaultPluginPathMacVST2() {
    RuntimePlatform platform = new RuntimePlatform("mac-x64", OperatingSystem.MAC, "x64");
    when(mockResolver.getCurrentPlatform()).thenReturn(platform);

    String path = applicationDefaults.getDefaultPluginPath(PluginFormat.VST2);
    assertEquals("/Library/Audio/Plug-ins/VST", path);
  }

  @Test
  public void testGetDefaultPluginPathMacVST3() {
    RuntimePlatform platform = new RuntimePlatform("mac-x64", OperatingSystem.MAC, "x64");
    when(mockResolver.getCurrentPlatform()).thenReturn(platform);

    String path = applicationDefaults.getDefaultPluginPath(PluginFormat.VST3);
    assertEquals("/Library/Audio/Plug-ins/VST3", path);
  }

  @Test
  public void testGetDefaultPluginPathMacAU() {
    RuntimePlatform platform = new RuntimePlatform("mac-x64", OperatingSystem.MAC, "x64");
    when(mockResolver.getCurrentPlatform()).thenReturn(platform);

    String path = applicationDefaults.getDefaultPluginPath(PluginFormat.AU);
    assertEquals("/Library/Audio/Plug-ins/Components", path);
  }

  @Test
  public void testGetDefaultPluginPathMacLV2() {
    RuntimePlatform platform = new RuntimePlatform("mac-x64", OperatingSystem.MAC, "x64");
    when(mockResolver.getCurrentPlatform()).thenReturn(platform);

    String path = applicationDefaults.getDefaultPluginPath(PluginFormat.LV2);
    assertEquals("/Library/Audio/Plug-Ins/LV2", path);
  }

  @Test
  public void testGetDefaultPluginPathLinuxVST2() {
    RuntimePlatform platform = new RuntimePlatform("linux-x64", OperatingSystem.LINUX, "x64");
    when(mockResolver.getCurrentPlatform()).thenReturn(platform);

    String path = applicationDefaults.getDefaultPluginPath(PluginFormat.VST2);
    assertEquals("/usr/lib/vst", path);
  }

  @Test
  public void testGetDefaultPluginPathLinuxVST3() {
    RuntimePlatform platform = new RuntimePlatform("linux-x64", OperatingSystem.LINUX, "x64");
    when(mockResolver.getCurrentPlatform()).thenReturn(platform);

    String path = applicationDefaults.getDefaultPluginPath(PluginFormat.VST3);
    assertEquals("/usr/lib/vst3", path);
  }

  @Test
  public void testGetDefaultPluginPathLinuxLV2() {
    RuntimePlatform platform = new RuntimePlatform("linux-x64", OperatingSystem.LINUX, "x64");
    when(mockResolver.getCurrentPlatform()).thenReturn(platform);

    String path = applicationDefaults.getDefaultPluginPath(PluginFormat.LV2);
    assertEquals("/usr/lib/lv2", path);
  }

  @Test
  public void testGetDefaultPluginPathUnknownOS() {
    RuntimePlatform platform = new RuntimePlatform("unknown", OperatingSystem.UNKNOWN, "x64");
    when(mockResolver.getCurrentPlatform()).thenReturn(platform);

    String path = applicationDefaults.getDefaultPluginPath(PluginFormat.VST2);
    assertEquals("/path/to/audio/plugins", path);
  }

  @Test
  public void testGetVersion() {
    when(mockEnv.getProperty("owlplug.version")).thenReturn("1.0.0");
    String version = applicationDefaults.getVersion();
    assertEquals("1.0.0", version);
  }

  @Test
  public void testGetOwlPlugHubUrl() {
    when(mockEnv.getProperty("owlplug.hub.url")).thenReturn("https://hub.owlplug.com");
    String url = applicationDefaults.getOwlPlugHubUrl();
    assertEquals("https://hub.owlplug.com", url);
  }

  @Test
  public void testGetUpdateDownloadUrl() {
    when(mockEnv.getProperty("owlplug.hub.updateDownloadUrl")).thenReturn("https://download.owlplug.com");
    String url = applicationDefaults.getUpdateDownloadUrl();
    assertEquals("https://download.owlplug.com", url);
  }

  @Test
  public void testGetOwlPlugRegistryUrl() {
    when(mockEnv.getProperty("owlplug.registry.url")).thenReturn("https://registry.owlplug.com");
    String url = applicationDefaults.getOwlPlugRegistryUrl();
    assertEquals("https://registry.owlplug.com", url);
  }

  @Test
  public void testGetOpenAudioRegistryUrl() {
    when(mockEnv.getProperty("openaudio.registry.url")).thenReturn("https://registry.openaudio.io");
    String url = applicationDefaults.getOpenAudioRegistryUrl();
    assertEquals("https://registry.openaudio.io", url);
  }

  @Test
  public void testGetEnvProperty() {
    when(mockEnv.getProperty("custom.property")).thenReturn("custom-value");
    String value = applicationDefaults.getEnvProperty("custom.property");
    assertEquals("custom-value", value);
  }

  @Test
  public void testGetContributors() {
    List<String> contributors = ApplicationDefaults.getContributors();
    assertNotNull(contributors);
    assertTrue(contributors.size() > 0);
  }

  @Test
  public void testGetContributorsCached() {
    List<String> contributors1 = ApplicationDefaults.getContributors();
    List<String> contributors2 = ApplicationDefaults.getContributors();

    // Both calls should return the same data (cached)
    assertEquals(contributors1.size(), contributors2.size());
  }

  @Test
  public void testGetUserDataDirectory() {
    String userDataDir = ApplicationDefaults.getUserDataDirectory();
    assertNotNull(userDataDir);
    assertTrue(userDataDir.contains(".owlplug"));
  }

  @Test
  public void testGetTempDownloadDirectory() {
    String tempDir = ApplicationDefaults.getTempDownloadDirectory();
    assertNotNull(tempDir);
    assertTrue(tempDir.contains(".owlplug"));
    assertTrue(tempDir.contains("temp"));
  }

  @Test
  public void testGetLogDirectory() {
    String logDir = ApplicationDefaults.getLogDirectory();
    assertNotNull(logDir);
    assertTrue(logDir.contains(".owlplug"));
    assertTrue(logDir.contains("logs"));
  }

  @Test
  public void testGetRuntimePlatform() {
    RuntimePlatform platform = new RuntimePlatform("linux-x64", OperatingSystem.LINUX, "x64");
    when(mockResolver.getCurrentPlatform()).thenReturn(platform);

    RuntimePlatform result = applicationDefaults.getRuntimePlatform();
    assertEquals(platform, result);
  }
}