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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.explore.model.PackageTag;
import com.owlplug.explore.model.RemotePackage;
import java.util.Arrays;
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

}
