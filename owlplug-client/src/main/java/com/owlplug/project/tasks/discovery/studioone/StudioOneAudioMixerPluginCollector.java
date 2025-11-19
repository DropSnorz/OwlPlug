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

public class StudioOneAudioMixerPluginCollector {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private Document document;

  public StudioOneAudioMixerPluginCollector(Document document) {
    this.document = document;
  }

  public List<DawPlugin> collectPlugins() {

    ArrayList<DawPlugin> plugins = new ArrayList<>();

    XPath xpath = XPathFactory.newInstance().newXPath();
    try {
      Element insertsNode = findInsertsNode(document, xpath);
      
      // Find FX nodes - either as children of Inserts, or anywhere with name starting with "FX"
      NodeList fxNodes;
      if (insertsNode != null) {
        fxNodes = (NodeList) xpath.compile("./Attributes[starts-with(@name, 'FX')]")
                .evaluate(insertsNode, XPathConstants.NODESET);
      } else {
        fxNodes = (NodeList) xpath.compile("//Attributes[starts-with(@name, 'FX')]")
                .evaluate(document, XPathConstants.NODESET);
      }

      for (int i = 0; i < fxNodes.getLength(); i++) {
        Node node = fxNodes.item(i);
        if (node instanceof Element element) {
          // Filter out empty FX slots by checking for deviceData or ghostData children
          StudioOneDomUtils.DeviceDataAndGhostData data = StudioOneDomUtils.findDeviceDataAndGhostData(element);
          if (data.getDeviceData() == null && data.getGhostData() == null) {
            continue; // Skip empty FX slots
          }
          
          DawPlugin plugin = readPluginElement(element);
          if (plugin != null) {
            plugins.add(plugin);
          }
        }
      }

    } catch (XPathExpressionException e) {
      log.error("Error extracting plugins from audio mixer", e);
    }

    return plugins;
  }

  private Element findInsertsNode(Document document, XPath xpath) throws XPathExpressionException {
    // Find the Inserts node by checking x:id attribute directly
    // Note: x:id is a literal attribute name (colon is part of the name, not a namespace)
    NodeList allAttributes = (NodeList) xpath.compile("//Attributes")
            .evaluate(document, XPathConstants.NODESET);
    for (int i = 0; i < allAttributes.getLength(); i++) {
      Node node = allAttributes.item(i);
      if (node instanceof Element elem) {
        String xidValue = elem.getAttribute("x:id");
        if ("Inserts".equals(xidValue)) {
          return elem;
        }
      }
    }
    return null;
  }

  private DawPlugin readPluginElement(Element pluginElement) {
    String pluginName = extractPluginName(pluginElement);
    if (pluginName == null || pluginName.isEmpty()) {
      return null;
    }

    PluginFormat format = extractPluginFormat(pluginElement);
    if (format == null) {
      return null;
    }

    DawPlugin plugin = new DawPlugin();
    plugin.setName(pluginName);
    plugin.setFormat(format);
    return plugin;
  }

  private String extractPluginName(Element pluginElement) {
    // For audio effect plugins, prefer classInfo over deviceData
    // because deviceData may contain instance names (e.g., "Ozone 9", "Ozone 9 (2)")
    // while classInfo always contains the actual plugin name
    StudioOneDomUtils.DeviceDataAndGhostData data = StudioOneDomUtils.findDeviceDataAndGhostData(pluginElement);
    
    // Try classInfo FIRST (it has the actual plugin name)
    Element classInfo = StudioOneDomUtils.findClassInfo(data.getGhostData());
    if (classInfo != null) {
      String name = classInfo.getAttribute("name");
      if (name != null && !name.isEmpty()) {
        return name;
      }
    }

    // Fallback to deviceData only if classInfo didn't work
    if (data.getDeviceData() != null) {
      String name = data.getDeviceData().getAttribute("name");
      if (name != null && !name.isEmpty()) {
        return name;
      }
    }

    return null;
  }

  private PluginFormat extractPluginFormat(Element pluginElement) {
    StudioOneDomUtils.DeviceDataAndGhostData data = StudioOneDomUtils.findDeviceDataAndGhostData(pluginElement);
    Element classInfo = StudioOneDomUtils.findClassInfo(data.getGhostData());
    
    if (classInfo != null) {
      String subCategory = classInfo.getAttribute("subCategory");
      if (subCategory != null && !subCategory.isEmpty()) {
        // Skip native Studio One plugins
        if (subCategory.equals("(Native)")) {
          return null;
        }

        // Determine format from subCategory (format is like "VST2/Mastering" or "VST3/Mastering")
        if (subCategory.startsWith("VST2") || subCategory.contains("VST2")) {
          return PluginFormat.VST2;
        } else if (subCategory.startsWith("VST3") || subCategory.contains("VST3")) {
          return PluginFormat.VST3;
        }
      }
    }

    return null;
  }

}

