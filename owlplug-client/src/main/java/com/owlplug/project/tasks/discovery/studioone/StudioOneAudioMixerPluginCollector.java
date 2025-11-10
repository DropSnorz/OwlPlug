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
      // Find all FX insert slots in the audio mixer
      // Note: x:id is a literal attribute name (colon is part of the name, not a namespace)
      // We'll use DOM traversal to find nodes with x:id attributes since XPath has issues with literal colons
      
      // Find the Inserts node by checking x:id attribute directly
      NodeList allAttributes = (NodeList) xpath.compile("//Attributes")
              .evaluate(document, XPathConstants.NODESET);
      Element insertsNode = null;
      for (int i = 0; i < allAttributes.getLength(); i++) {
        Node node = allAttributes.item(i);
        if (node instanceof Element elem) {
          String xidValue = elem.getAttribute("x:id");
          if ("Inserts".equals(xidValue)) {
            insertsNode = elem;
            break;
          }
        }
      }
      
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
          // Filter out empty FX slots by checking for deviceData or ghostData children via DOM
          boolean hasPlugin = false;
          NodeList children = element.getChildNodes();
          for (int j = 0; j < children.getLength(); j++) {
            Node child = children.item(j);
            if (child instanceof Element childElem && "Attributes".equals(childElem.getTagName())) {
              String xid = childElem.getAttribute("x:id");
              if ("deviceData".equals(xid) || "ghostData".equals(xid)) {
                hasPlugin = true;
                break;
              }
            }
          }
          
          if (!hasPlugin) {
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

  private DawPlugin readPluginElement(Element pluginElement) {

    // For audio effect plugins, prefer classInfo over deviceData
    // because deviceData may contain instance names (e.g., "Ozone 9", "Ozone 9 (2)")
    // while classInfo always contains the actual plugin name
    String pluginName = null;
    
    // Traverse children to find deviceData and ghostData elements
    NodeList children = pluginElement.getChildNodes();
    Element deviceData = null;
    Element ghostData = null;
    
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element childElem && "Attributes".equals(childElem.getTagName())) {
        String xid = childElem.getAttribute("x:id");
        if ("deviceData".equals(xid)) {
          deviceData = childElem;
        } else if ("ghostData".equals(xid)) {
          ghostData = childElem;
        }
      }
    }
    
    // Try classInfo FIRST (it has the actual plugin name)
    if (ghostData != null) {
      NodeList ghostChildren = ghostData.getChildNodes();
      for (int i = 0; i < ghostChildren.getLength(); i++) {
        Node ghostChild = ghostChildren.item(i);
        if (ghostChild instanceof Element ghostChildElem && "Attributes".equals(ghostChildElem.getTagName())) {
          String xid = ghostChildElem.getAttribute("x:id");
          if ("classInfo".equals(xid)) {
            pluginName = ghostChildElem.getAttribute("name");
            if (pluginName != null && !pluginName.isEmpty()) {
              break;
            }
          }
        }
      }
    }

    // Fallback to deviceData only if classInfo didn't work
    if ((pluginName == null || pluginName.isEmpty()) && deviceData != null) {
      pluginName = deviceData.getAttribute("name");
    }

    if (pluginName == null || pluginName.isEmpty()) {
      return null;
    }

    // Get plugin format from subCategory (using DOM directly)
    PluginFormat format = null;
    Element classInfo = null;
    
    // Find classInfo element (we may have already found ghostData above)
    if (ghostData == null) {
      // Reuse the children NodeList we already have
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        if (child instanceof Element childElem && "Attributes".equals(childElem.getTagName())) {
          String xid = childElem.getAttribute("x:id");
          if ("ghostData".equals(xid)) {
            ghostData = childElem;
            break;
          }
        }
      }
    }
    
    if (ghostData != null) {
      NodeList ghostChildren = ghostData.getChildNodes();
      for (int i = 0; i < ghostChildren.getLength(); i++) {
        Node ghostChild = ghostChildren.item(i);
        if (ghostChild instanceof Element ghostChildElem && "Attributes".equals(ghostChildElem.getTagName())) {
          String xid = ghostChildElem.getAttribute("x:id");
          if ("classInfo".equals(xid)) {
            classInfo = ghostChildElem;
            break;
          }
        }
      }
    }
    
    if (classInfo != null) {
      String subCategory = classInfo.getAttribute("subCategory");
      if (subCategory != null && !subCategory.isEmpty()) {
        // Skip native Studio One plugins
        if (subCategory.equals("(Native)")) {
          return null;
        }

        // Determine format from subCategory (format is like "VST2/Mastering" or "VST3/Mastering")
        if (subCategory.startsWith("VST2") || subCategory.contains("VST2")) {
          format = PluginFormat.VST2;
        } else if (subCategory.startsWith("VST3") || subCategory.contains("VST3")) {
          format = PluginFormat.VST3;
        }
      }
    }

    if (format == null) {
      return null;
    }

    DawPlugin plugin = new DawPlugin();
    plugin.setName(pluginName);
    plugin.setFormat(format);

    return plugin;
  }

}

