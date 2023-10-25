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

import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.utils.DomUtils;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.project.model.DawApplication;
import com.owlplug.project.model.Project;
import com.owlplug.project.model.ProjectPlugin;
import com.owlplug.project.tasks.discovery.ProjectExplorerException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AbletonProjectExplorer {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  public boolean canExploreFile(File file) {
    return file.isFile() && file.getAbsolutePath().endsWith(".als");
  }

  public Project explore(File file) throws ProjectExplorerException {

    if (!canExploreFile(file)) {
      return null;
    }

    try {

      Document xmlDocument = createDocument(file);
      XPath xPath = XPathFactory.newInstance().newXPath();
      Project project = new Project();
      project.setApplication(DawApplication.ABLETON);
      project.setPath(FileUtils.convertPath(file.getAbsolutePath()));
      project.setName(FilenameUtils.removeExtension(file.getName()));
      NodeList abletonNode = (NodeList) xPath.compile("/Ableton").evaluate(xmlDocument, XPathConstants.NODESET);
      project.setAppFullName(abletonNode.item(0).getAttributes().getNamedItem("Creator").getNodeValue());

      AbletonSchema5PluginCollector collector = new AbletonSchema5PluginCollector(xmlDocument);
      List<ProjectPlugin> plugins = collector.collectPlugins();

      for (ProjectPlugin plugin : plugins) {
        plugin.setProject(project);
        project.getPlugins().add(plugin);
      }

      return project;

    } catch (XPathExpressionException e) {
      throw new ProjectExplorerException("Error while parsing project file " + file.getAbsolutePath(), e);
    }

  }

  private Document createDocument(File file) throws ProjectExplorerException {

    try (InputStream fi = new FileInputStream(file);
         InputStream bi = new BufferedInputStream(fi);
         CompressorInputStream gzi = new CompressorStreamFactory().createCompressorInputStream(bi);
         InputStream bgzi = new BufferedInputStream(gzi)) {

      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = builderFactory.newDocumentBuilder();
      return builder.parse(bgzi);

    } catch (FileNotFoundException e) {
      throw new ProjectExplorerException("Project file not found: " + file.getAbsolutePath(), e);
    } catch (CompressorException e) {
      throw new ProjectExplorerException("Error while uncompressing project file: " + file.getAbsolutePath(), e);
    } catch (IOException | ParserConfigurationException | SAXException e) {
      throw new ProjectExplorerException("Unexpected error while reading project file: {}", e);
    }
  }

}
