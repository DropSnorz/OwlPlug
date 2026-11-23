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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.explore.model.BundleType;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.PackageTag;
import com.owlplug.explore.model.RemotePackage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
  public void testArchiveAndInstallerFilesShouldBeMappedWithTheirType() {
    OASPlugin plugin = new OASPlugin();
    plugin.setName("Plugin");
    plugin.setFiles(List.of(file("archive", "vst3"), file("installer", "vst3")));

    RemotePackage remotePackage = OASModelAdapter.mapperToEntity(plugin);

    Map<Integer, BundleType> typesByOrder = remotePackage.getBundles().stream()
        .collect(Collectors.toMap(PackageBundle::getOrder, PackageBundle::getType));
    assertEquals(Map.of(0, BundleType.ARCHIVE, 1, BundleType.INSTALLER), typesByOrder);
  }

  @Test
  public void testUnknownFileTypesShouldBeSkippedKeepingRegistryOrder() {
    OASPlugin plugin = new OASPlugin();
    plugin.setName("Plugin");
    plugin.setFiles(Arrays.asList(file("source", "vst3"), file(null, "vst3"), file("Installer", "vst3")));

    RemotePackage remotePackage = OASModelAdapter.mapperToEntity(plugin);

    assertEquals(1, remotePackage.getBundles().size());
    PackageBundle bundle = remotePackage.getBundles().iterator().next();
    assertEquals(BundleType.INSTALLER, bundle.getType());
    // Order is the index in the unfiltered files array, used to re-fetch download details
    assertEquals(2, bundle.getOrder());
  }

  @Test
  public void testFilesWithoutSupportedFormatsShouldBeSkipped() {
    OASFile withoutContains = file("archive");
    withoutContains.setContains(null);
    OASPlugin plugin = new OASPlugin();
    plugin.setName("Plugin");
    plugin.setFiles(List.of(withoutContains, file("installer", "clap")));

    RemotePackage remotePackage = OASModelAdapter.mapperToEntity(plugin);

    assertTrue(remotePackage.getBundles().isEmpty());
  }

  @Test
  public void testOasTypeShouldMapToBundleType() {
    assertEquals(BundleType.ARCHIVE, BundleType.fromOasType("archive"));
    assertEquals(BundleType.INSTALLER, BundleType.fromOasType(" INSTALLER "));
    assertNull(BundleType.fromOasType("source"));
    assertNull(BundleType.fromOasType(null));
  }

  private OASFile file(String type, String... contains) {
    OASFile.System system = new OASFile.System();
    system.setType("win");
    OASFile file = new OASFile();
    file.setType(type);
    file.setSystems(List.of(system));
    file.setArchitectures(List.of("x64"));
    file.setContains(List.of(contains));
    file.setSha256("abc");
    file.setUrl("https://example.com/plugin");
    return file;
  }

  @Test
  public void testPluginWithoutTagsShouldHaveNoTags() {
    OASPlugin plugin = new OASPlugin();
    plugin.setName("Plugin");

    RemotePackage remotePackage = OASModelAdapter.mapperToEntity(plugin);

    assertTrue(remotePackage.getTags().isEmpty());
  }

}
