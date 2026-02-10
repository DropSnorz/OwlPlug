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

package com.owlplug.project.tasks.discovery.studioone;

import com.owlplug.project.model.DawPlugin;
import java.io.File;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StudioOneAudioMixerPluginCollectorTest {

  @Test
  public void testCollectPlugins() throws Exception {
    File testFile = new File(this.getClass().getClassLoader()
                                 .getResource("projects/studioone/files/audiomixer.xml").getFile());
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(testFile);

    StudioOneAudioMixerPluginCollector collector = new StudioOneAudioMixerPluginCollector(document);
    List<DawPlugin> plugins = collector.collectPlugins();

    assertNotNull(plugins);
    assertEquals(3, plugins.size());

    DawPlugin firstPlugin = plugins.get(0);
    assertEquals("Reverb", firstPlugin.getName());
    assertEquals("VST2", firstPlugin.getFormat().name());
  }
}