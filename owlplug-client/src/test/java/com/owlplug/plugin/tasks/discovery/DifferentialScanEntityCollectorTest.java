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

package com.owlplug.plugin.tasks.discovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.Symlink;
import com.owlplug.plugin.tasks.discovery.fileformats.PluginFile;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DifferentialScanEntityCollectorTest {

  @Test
  void differentialPluginsDetectsAddedAndRemoved() {
    PluginScanTaskParameters params = mock(PluginScanTaskParameters.class);
    DifferentialScanEntityCollector collector = spy(new DifferentialScanEntityCollector(params));

    PluginFile newFile = mock(PluginFile.class);
    when(newFile.getPath()).thenReturn("/plugins/a.jar");
    doReturn(Set.of(newFile)).when(collector).getPluginFiles();

    Plugin oldPlugin = mock(Plugin.class);
    when(oldPlugin.getPath()).thenReturn("/plugins/b.jar");
    List<Plugin> persisted = List.of(oldPlugin);

    collector.differentialPlugins(persisted);

    DifferentialScanEntityCollector.PluginFileDifferential diff = collector.getPluginDifferential();
    assertNotNull(diff);
    assertEquals(1, diff.getAdded().size(), "expected one added plugin file");
    assertSame(newFile, diff.getAdded().getFirst());
    assertEquals(1, diff.getRemoved().size(), "expected one removed plugin path");
    assertEquals("/plugins/b.jar", diff.getRemoved().getFirst());
  }

  @Test
  void differentialPluginsNoChangesResultsEmptyDifferential() {
    PluginScanTaskParameters params = mock(PluginScanTaskParameters.class);
    DifferentialScanEntityCollector collector = spy(new DifferentialScanEntityCollector(params));

    PluginFile file = mock(PluginFile.class);
    when(file.getPath()).thenReturn("/plugins/same.jar");
    doReturn(Set.of(file)).when(collector).getPluginFiles();

    Plugin persistedPlugin = mock(Plugin.class);
    when(persistedPlugin.getPath()).thenReturn("/plugins/same.jar");
    List<Plugin> persisted = List.of(persistedPlugin);

    collector.differentialPlugins(persisted);

    DifferentialScanEntityCollector.PluginFileDifferential diff = collector.getPluginDifferential();
    assertNotNull(diff);
    assertTrue(diff.getAdded().isEmpty(), "no added files expected");
    assertTrue(diff.getRemoved().isEmpty(), "no removed paths expected");
  }

  @Test
  void differentialSymlinksDetectsAddedAndRemoved() {
    PluginScanTaskParameters params = mock(PluginScanTaskParameters.class);
    DifferentialScanEntityCollector collector = spy(new DifferentialScanEntityCollector(params));

    Symlink newLink = mock(Symlink.class);
    when(newLink.getPath()).thenReturn("/links/new");
    doReturn(Set.of(newLink)).when(collector).getSymlinks();

    Symlink oldLink = mock(Symlink.class);
    when(oldLink.getPath()).thenReturn("/links/old");
    List<Symlink> persisted = List.of(oldLink);

    collector.differentialSymlinks(persisted);

    DifferentialScanEntityCollector.SymlinkDifferential diff = collector.getSymlinkDifferential();
    assertNotNull(diff);
    assertEquals(1, diff.getAdded().size(), "expected one added symlink");
    assertSame(newLink, diff.getAdded().getFirst());
    assertEquals(1, diff.getRemoved().size(), "expected one removed symlink path");
    assertEquals("/links/old", diff.getRemoved().getFirst());
  }

  @Test
  void differentialSymlinksNoChangesResultsEmptyDifferential() {
    PluginScanTaskParameters params = mock(PluginScanTaskParameters.class);
    DifferentialScanEntityCollector collector = spy(new DifferentialScanEntityCollector(params));

    Symlink link = mock(Symlink.class);
    when(link.getPath()).thenReturn("/links/same");
    doReturn(Set.of(link)).when(collector).getSymlinks();

    Symlink persistedLink = mock(Symlink.class);
    when(persistedLink.getPath()).thenReturn("/links/same");
    List<Symlink> persisted = List.of(persistedLink);

    collector.differentialSymlinks(persisted);

    DifferentialScanEntityCollector.SymlinkDifferential diff = collector.getSymlinkDifferential();
    assertNotNull(diff);
    assertTrue(diff.getAdded().isEmpty(), "no added symlinks expected");
    assertTrue(diff.getRemoved().isEmpty(), "no removed symlink paths expected");
  }
}