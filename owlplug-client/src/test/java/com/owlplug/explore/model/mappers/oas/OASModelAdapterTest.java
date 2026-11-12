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

package com.owlplug.explore.model.mappers.oas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.PackageTag;
import com.owlplug.explore.model.RemotePackage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class OASModelAdapterTest {

  @Test
  public void testPluginTagsShouldBeTrimmedAndDeduplicated() {
    OASPlugin plugin = new OASPlugin();
    plugin.setName("Plugin");
    plugin.setTags(Arrays.asList("Reverb", " reverb ", "EQ ", "", "  ", null, "Delay"));

    RemotePackage remotePackage = OASModelAdapter.mapperToEntity(plugin);

    Set<String> tagNames = remotePackage.getTags().stream().map(PackageTag::getName).collect(Collectors.toSet());
    assertEquals(Set.of("Reverb", "EQ", "Delay"), tagNames);
    for (PackageTag tag : remotePackage.getTags()) {
      assertSame(remotePackage, tag.getRemotePackage());
    }
  }

  @Test
  public void testPluginWithoutTagsShouldHaveNoTags() {
    OASPlugin plugin = new OASPlugin();
    plugin.setName("Plugin");

    RemotePackage remotePackage = OASModelAdapter.mapperToEntity(plugin);

    assertTrue(remotePackage.getTags().isEmpty());
  }

  @Test
  public void testInstallerFileShouldBeMappedToInstallerBundle() {
    OASFile file = new OASFile();
    file.setType("installer");
    OASFile.System system = new OASFile.System();
    system.setType("mac");
    file.setSystems(List.of(system));
    file.setArchitectures(List.of("arm64"));
    file.setContains(List.of("vst3", "component"));
    file.setUrl("https://example.test/plugin.dmg");
    file.setSha256("abc");
    file.setSize(123L);

    OASPlugin plugin = new OASPlugin();
    plugin.setName("Plugin");
    plugin.setFiles(List.of(file));

    RemotePackage remotePackage = OASModelAdapter.mapperToEntity(plugin);

    assertEquals(1, remotePackage.getBundles().size());
    PackageBundle bundle = remotePackage.getBundles().iterator().next();
    assertTrue(bundle.isInstaller());
    assertEquals(List.of("mac-arm64"), bundle.getTargets());
    assertEquals(Set.of("vst3", "au"), new HashSet<>(bundle.getFormats()));
  }

  @Test
  public void testArchiveFileShouldNotBeMarkedAsInstaller() {
    OASFile file = new OASFile();
    file.setType("archive");
    OASFile.System system = new OASFile.System();
    system.setType("linux");
    file.setSystems(List.of(system));
    file.setArchitectures(List.of("x64"));
    file.setContains(List.of("vst3"));
    file.setUrl("https://example.test/plugin.zip");
    file.setSha256("abc");
    file.setSize(123L);

    OASPlugin plugin = new OASPlugin();
    plugin.setName("Plugin");
    plugin.setFiles(List.of(file));

    RemotePackage remotePackage = OASModelAdapter.mapperToEntity(plugin);

    assertEquals(1, remotePackage.getBundles().size());
    assertFalse(remotePackage.getBundles().iterator().next().isInstaller());
  }

  @Test
  public void testUnsupportedFileTypesShouldBeSkipped() {
    OASFile installerWithoutFormat = new OASFile();
    installerWithoutFormat.setType("installer");
    OASFile.System system = new OASFile.System();
    system.setType("win");
    installerWithoutFormat.setSystems(List.of(system));
    installerWithoutFormat.setArchitectures(List.of("x64"));
    installerWithoutFormat.setContains(List.of("app"));
    installerWithoutFormat.setUrl("https://example.test/plugin.msi");

    OASFile unknownType = new OASFile();
    unknownType.setType("other");
    system = new OASFile.System();
    system.setType("linux");
    unknownType.setSystems(List.of(system));
    unknownType.setArchitectures(List.of("x64"));
    unknownType.setContains(List.of("vst3"));
    unknownType.setUrl("https://example.test/plugin.zip");

    OASFile nullType = new OASFile();
    nullType.setSystems(List.of(system));
    nullType.setArchitectures(List.of("x64"));
    nullType.setContains(List.of("vst3"));
    nullType.setUrl("https://example.test/plugin.zip");

    for (OASFile file : List.of(installerWithoutFormat, unknownType, nullType)) {
      OASPlugin plugin = new OASPlugin();
      plugin.setName("Plugin");
      plugin.setFiles(List.of(file));
      assertTrue(OASModelAdapter.mapperToEntity(plugin).getBundles().isEmpty());
    }
  }

}
