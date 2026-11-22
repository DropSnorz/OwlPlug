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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug. If not, see <https://www.gnu.org/licenses/>.
 */

package com.owlplug.project.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import com.owlplug.plugin.repositories.PluginComponentRepository;
import com.owlplug.project.model.DawPlugin;
import com.owlplug.project.model.DawPluginLookup;
import com.owlplug.project.model.LookupResult;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class PluginLookupServiceTest {

  @Test
  public void doesNotQueryComponentsForBlankPersistedNames() {
    PluginComponentRepository components = mock(PluginComponentRepository.class);
    PluginLookupService service = new PluginLookupService();
    ReflectionTestUtils.setField(service, "pluginComponentRepository", components);

    for (String name : new String[] {null, "", "   "}) {
      DawPlugin plugin = new DawPlugin();
      plugin.setName(name);

      DawPluginLookup lookup = service.lookup(plugin);

      assertEquals(LookupResult.MISSING, lookup.getResult());
      assertNull(lookup.getComponent());
      assertSame(plugin, lookup.getDawPlugin());
    }
    verifyNoInteractions(components);
  }
}
