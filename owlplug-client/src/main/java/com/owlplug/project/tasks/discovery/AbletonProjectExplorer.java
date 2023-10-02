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

package com.owlplug.project.tasks.discovery;

import com.owlplug.project.model.Project;
import com.owlplug.project.model.ProjectPlugin;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AbletonProjectExplorer {

  public boolean canExploreFile(File file) {
    return file.getAbsolutePath().endsWith(".als");
  }

  public Project explore(File file) {

    try {

      Document xmlDocument = createDocument(file);
      XPath xPath = XPathFactory.newInstance().newXPath();
      Project project = new Project();
      NodeList abletonNode = (NodeList) xPath.compile("/Ableton").evaluate(xmlDocument, XPathConstants.NODESET);
      project.setAppName(abletonNode.item(0).getAttributes().getNamedItem("Creator").getNodeValue());

      NodeList vstPlugins = (NodeList) xPath.compile("//PluginDevice/PluginDesc/VstPluginInfo").evaluate(xmlDocument, XPathConstants.NODESET);


      for (int i = 0; i < vstPlugins.getLength();i++) {
        System.out.println(vstPlugins.item(i).toString());

        Node node = vstPlugins.item(i);

        if (node instanceof Element element) {
          project.getPlugins().add(readPluginElement(element));
        }

      }
      return project;

    } catch (XPathExpressionException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

  }

  private ProjectPlugin readPluginElement(Element pluginElement) {

    ProjectPlugin plugin = new ProjectPlugin();
    NodeList fileNameNodes = pluginElement.getElementsByTagName("FileName");

    if (fileNameNodes.getLength() >= 1) {
      plugin.setFileName(fileNameNodes.item(0).getAttributes().getNamedItem("Value").getNodeValue());

    }

    NodeList nameNodes = pluginElement.getElementsByTagName("PlugName");
    if (nameNodes.getLength() >= 1) {
      plugin.setName(nameNodes.item(0).getAttributes().getNamedItem("Value").getNodeValue());

    }

    NodeList uniqueIdNode = pluginElement.getElementsByTagName("UniqueId");
    if (uniqueIdNode.getLength() >= 1) {
      plugin.setUid(uniqueIdNode.item(0).getAttributes().getNamedItem("Value").getNodeValue());

    }

    return plugin;
  }

  private Document createDocument(File file) {

    try (InputStream fi = new FileInputStream(file);
         InputStream bi = new BufferedInputStream(fi);
         CompressorInputStream gzi = new CompressorStreamFactory().createCompressorInputStream(bi);
         InputStream bgzi = new BufferedInputStream(gzi)) {

      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = builderFactory.newDocumentBuilder();
      return builder.parse(bgzi);

    } catch (CompressorException | FileNotFoundException e) {
      //TODO: auto-generated
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (IOException e) {
      //TODO: auto-generated
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }

  }

}
