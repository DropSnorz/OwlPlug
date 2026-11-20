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

package com.owlplug.project.tasks.discovery.dawproject;

import com.owlplug.core.utils.PluginUtils;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.project.model.DawPlugin;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

final class DawprojectXmlReader {

  record ProjectContent(String version, String applicationName, String applicationVersion,
                        List<DawPlugin> plugins) {
  }

  ProjectContent readProject(InputStream input) throws XMLStreamException {
    XMLStreamReader xml = createReader(input);
    List<DawPlugin> plugins = new ArrayList<>();
    String version = null;
    String applicationName = null;
    String applicationVersion = null;
    boolean rootSeen = false;

    try {
      while (xml.hasNext()) {
        if (xml.next() != XMLStreamConstants.START_ELEMENT) {
          continue;
        }

        String element = xml.getLocalName();
        if (!rootSeen) {
          if (!"Project".equals(element)) {
            throw new XMLStreamException("Expected a DAWproject Project element");
          }
          version = xml.getAttributeValue(null, "version");
          rootSeen = true;
        } else if ("Application".equals(element)) {
          applicationName = xml.getAttributeValue(null, "name");
          applicationVersion = xml.getAttributeValue(null, "version");
        } else {
          PluginFormat format = pluginFormat(element);
          if (format == null) {
            continue;
          }

          String uid = xml.getAttributeValue(null, "deviceID");
          String name = xml.getAttributeValue(null, "deviceName");
          if (name == null || name.isBlank()) {
            name = uid;
          }
          if (name == null || name.isBlank()) {
            continue;
          }

          DawPlugin plugin = new DawPlugin();
          plugin.setName(PluginUtils.absoluteName(name.strip()));
          plugin.setUid(uid == null ? null : uid.strip());
          plugin.setFormat(format);
          plugins.add(plugin);
        }
      }
    } finally {
      xml.close();
    }

    if (version == null || version.isBlank()) {
      throw new XMLStreamException("Missing DAWproject format version");
    }
    return new ProjectContent(version, applicationName, applicationVersion, plugins);
  }

  String readTitle(InputStream input) throws XMLStreamException {
    XMLStreamReader xml = createReader(input);
    try {
      while (xml.hasNext()) {
        if (xml.next() == XMLStreamConstants.START_ELEMENT
            && "Title".equals(xml.getLocalName())) {
          return xml.getElementText();
        }
      }
      return null;
    } finally {
      xml.close();
    }
  }

  private PluginFormat pluginFormat(String element) {
    return switch (element) {
      case "Vst2Plugin" -> PluginFormat.VST2;
      case "Vst3Plugin" -> PluginFormat.VST3;
      case "AuPlugin" -> PluginFormat.AU;
      // CLAP references can be added when OwlPlug supports scanning installed CLAP plugins (#240).
      default -> null;
    };
  }

  private XMLStreamReader createReader(InputStream input) throws XMLStreamException {
    XMLInputFactory factory = XMLInputFactory.newFactory();
    factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    return factory.createXMLStreamReader(input);
  }
}
