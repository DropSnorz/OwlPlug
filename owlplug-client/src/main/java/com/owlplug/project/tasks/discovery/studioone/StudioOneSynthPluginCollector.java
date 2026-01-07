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

import com.owlplug.core.utils.PluginUtils;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.project.model.DawPlugin;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StudioOneSynthPluginCollector {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private Document document;

  public StudioOneSynthPluginCollector(Document document) {
    this.document = document;
  }

  public List<DawPlugin> collectPlugins() {

    ArrayList<DawPlugin> plugins = new ArrayList<>();

    XPath xpath = XPathFactory.newInstance().newXPath();
    try {
      // Find all instrument/synth attributes in the synth folder
      // These are typically Attributes elements with name and speakerFormat attributes
      NodeList synthNodes = (NodeList) xpath.compile("//Attributes[@name and @speakerFormat]")
              .evaluate(document, XPathConstants.NODESET);

      for (int i = 0; i < synthNodes.getLength(); i++) {
        Node node = synthNodes.item(i);
        if (node instanceof Element element) {
          DawPlugin plugin = readSynthElement(element);
          if (plugin != null) {
            plugins.add(plugin);
          }
        }
      }

    } catch (XPathExpressionException e) {
      log.error("Error extracting plugins from synth folder", e);
    }

    return plugins;
  }

  private DawPlugin readSynthElement(Element synthElement) {
    String pluginName = StudioOneDomUtils.extractPluginName(synthElement);
    if (pluginName == null || pluginName.isEmpty()) {
      return null;
    }

    PluginFormat format = StudioOneDomUtils.extractSynthPluginFormat(synthElement);
    if (format == null) {
      return null;
    }

    // Normalize the plugin name (remove platform suffixes like x64, x32, etc.)
    // This ensures cleaner data in the database and simplifies later queries
    String normalizedName = PluginUtils.absoluteName(pluginName);

    DawPlugin plugin = new DawPlugin();
    plugin.setName(normalizedName);
    plugin.setFormat(format);
    return plugin;
  }

}

