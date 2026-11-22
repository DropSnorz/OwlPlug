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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.owlplug.project.model.DawPlugin;
import java.io.File;
import java.io.StringReader;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class StudioOneSynthPluginCollectorTest {

  @Test
  public void testCollectPlugins() throws Exception {
    File testFile = new File(this.getClass().getClassLoader()
                                 .getResource("projects/studioone/files/audiosynthfolder.xml").getFile());
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(testFile);

    StudioOneSynthPluginCollector collector = new StudioOneSynthPluginCollector(document);
    List<DawPlugin> plugins = collector.collectPlugins();

    assertNotNull(plugins);
    assertEquals(2, plugins.size());

    DawPlugin firstPlugin = plugins.get(0);
    assertEquals("Sylenth1", firstPlugin.getName());
    assertEquals("VST2", firstPlugin.getFormat().name());
  }
  @Test
  public void preservesShortNamesAndStripsOrdinarySuffixes() throws Exception {
    String xml = """
        <Root xmlns:x="urn:test">
          <Attributes name="Synth1" speakerFormat="Stereo">
            <Attributes x:id="ghostData">
              <Attributes x:id="classInfo" name="64" subCategory="VST2"
                          category="AudioSynth"/>
            </Attributes>
          </Attributes>
          <Attributes name="Synth2" speakerFormat="Stereo">
            <Attributes x:id="ghostData">
              <Attributes x:id="classInfo" name="Synth_x64" subCategory="VST3"
                          category="AudioSynth"/>
            </Attributes>
          </Attributes>
          <Attributes name="Synth3" speakerFormat="Stereo">
            <Attributes x:id="ghostData">
              <Attributes x:id="classInfo" name="   " subCategory="VST3"
                          category="AudioSynth"/>
            </Attributes>
          </Attributes>
        </Root>
        """;
    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .parse(new InputSource(new StringReader(xml)));

    List<DawPlugin> plugins = new StudioOneSynthPluginCollector(document).collectPlugins();

    assertEquals(2, plugins.size());
    assertEquals("64", plugins.get(0).getName());
    assertEquals("Synth", plugins.get(1).getName());
  }
}
