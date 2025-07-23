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

package com.owlplug.project.tasks.discovery.ableton;

import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.core.utils.DomUtils;
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

public class AbletonSchema5PluginCollector {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private Document document;

  public AbletonSchema5PluginCollector(Document document) {
    this.document = document;
  }

  public List<DawPlugin> collectPlugins() {

    ArrayList<DawPlugin> plugins = new ArrayList<>();

    XPath xPath = XPathFactory.newInstance().newXPath();
    try {
      NodeList vstPlugins = (NodeList) xPath.compile("//PluginDevice/PluginDesc/VstPluginInfo")
              .evaluate(document, XPathConstants.NODESET);

      for (int i = 0; i < vstPlugins.getLength();i++) {
        Node node = vstPlugins.item(i);
        if (node instanceof Element element) {
          plugins.add(readVstPluginElement(element));
        }
      }

      NodeList vst3Plugins = (NodeList) xPath.compile("//PluginDevice/PluginDesc/Vst3PluginInfo")
              .evaluate(document, XPathConstants.NODESET);
      for (int i = 0; i < vst3Plugins.getLength();i++) {
        Node node = vst3Plugins.item(i);
        if (node instanceof Element element) {
          plugins.add(readVst3PluginElement(element));
        }
      }

      NodeList auPlugins = (NodeList) xPath.compile("//AuPluginDevice/PluginDesc/AuPluginInfo")
              .evaluate(document, XPathConstants.NODESET);
      for (int i = 0; i < auPlugins.getLength();i++) {
        Node node = auPlugins.item(i);
        if (node instanceof Element element) {
          plugins.add(readAuPluginElement(element));
        }
      }

    } catch (XPathExpressionException e) {
      log.error("Error extracting plugin", e);
    }

    return plugins;
  }

  private DawPlugin readVstPluginElement(Element pluginElement) {

    DawPlugin plugin = new DawPlugin();
    plugin.setFormat(PluginFormat.VST2);
    NodeList fileNameNodes = DomUtils.getDirectDescendantElementsByTagName(pluginElement, "FileName");
    if (fileNameNodes.getLength() >= 1) {
      plugin.setFileName(fileNameNodes.item(0).getAttributes().getNamedItem("Value").getNodeValue());

    }

    NodeList nameNodes = DomUtils.getDirectDescendantElementsByTagName(pluginElement, "PlugName");
    if (nameNodes.getLength() >= 1) {
      plugin.setName(nameNodes.item(0).getAttributes().getNamedItem("Value").getNodeValue());

    }

    NodeList uniqueIdNode = DomUtils.getDirectDescendantElementsByTagName(pluginElement, "UniqueId");
    if (uniqueIdNode.getLength() >= 1) {
      plugin.setUid(uniqueIdNode.item(0).getAttributes().getNamedItem("Value").getNodeValue());

    }

    return plugin;
  }

  private DawPlugin readVst3PluginElement(Element pluginElement) {

    DawPlugin plugin = new DawPlugin();
    plugin.setFormat(PluginFormat.VST3);
    NodeList nameNodes = DomUtils.getDirectDescendantElementsByTagName(pluginElement, "Name");
    if (nameNodes.getLength() >= 1) {
      plugin.setName(nameNodes.item(0).getAttributes().getNamedItem("Value").getNodeValue());

    }

    return plugin;
  }

  private DawPlugin readAuPluginElement(Element pluginElement) {

    DawPlugin plugin = new DawPlugin();
    plugin.setFormat(PluginFormat.AU);
    NodeList nameNodes = DomUtils.getDirectDescendantElementsByTagName(pluginElement, "Name");
    if (nameNodes.getLength() >= 1) {
      plugin.setName(nameNodes.item(0).getAttributes().getNamedItem("Value").getNodeValue());

    }

    return plugin;
  }



}
