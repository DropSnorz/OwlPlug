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

import com.owlplug.core.utils.ArchiveUtils;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.project.model.DawApplication;
import com.owlplug.project.model.DawPlugin;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.tasks.discovery.ProjectExplorer;
import com.owlplug.project.tasks.discovery.ProjectExplorerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class StudioOneProjectExplorer implements ProjectExplorer {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  public boolean canExploreFile(File file) {
    if (!file.isFile() || !file.getAbsolutePath().endsWith(".song")) {
      return false;
    }
    
    // Skip autosave files - they have "(Autosaved)" in the name or are in History folders
    String fileName = file.getName();
    String filePath = file.getAbsolutePath();
    
    if (fileName.contains("(Autosaved)") || filePath.contains("\\History\\") || filePath.contains("/History/")) {
      log.debug("Skipping autosave file: {}", file.getAbsolutePath());
      return false;
    }
    
    return true;
  }

  public DawProject explore(File file) throws ProjectExplorerException {

    if (!canExploreFile(file)) {
      return null;
    }

    log.debug("Starting exploring Studio One file: {}", file.getAbsoluteFile());

    Path tempDir = null;
    try {
      // Extract only the files we need from the ZIP archive to temporary directory
      // This avoids creating directories with reserved names (e.g., Windows "Strings" directory)
      tempDir = Files.createTempDirectory("studioone-");
      log.debug("Extracting Studio One project to: {}", tempDir);
      
      List<String> targetFiles = Arrays.asList(
          "metainfo.xml",
          "Devices/audiomixer.xml",
          "Devices/audiosynthfolder.xml"
      );
      
      try {
        ArchiveUtils.extract(file, tempDir.toFile(), targetFiles);
      } catch (IOException e) {
        log.warn("Failed to extract Studio One project file: {} - {}", 
            file.getAbsolutePath(), e.getMessage());
        return null; // Skip this file and continue with others
      }

      // Parse metainfo.xml for project metadata
      File metainfoFile = new File(tempDir.toFile(), "metainfo.xml");
      if (!metainfoFile.exists()) {
        throw new ProjectExplorerException(
            "metainfo.xml not found in Studio One project: " + file.getAbsolutePath(), null);
      }

      final Document metainfoDoc = createDocument(metainfoFile);
      final XPath xpath = XPathFactory.newInstance().newXPath();

      DawProject project = new DawProject();
      project.setApplication(DawApplication.STUDIO_ONE);
      project.setPath(FileUtils.convertPath(file.getAbsolutePath()));
      project.setName(FilenameUtils.removeExtension(file.getName()));

      extractMetadata(metainfoDoc, xpath, project);

      // Set file timestamps
      project.setLastModifiedAt(new Date(file.lastModified()));
      BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      FileTime fileTime = attr.creationTime();
      project.setCreatedAt(Date.from(fileTime.toInstant()));

      collectPlugins(tempDir, project);

      return project;

    } catch (IOException e) {
      throw new ProjectExplorerException(
          "Error while extracting Studio One project file: " + file.getAbsolutePath(), e);
    } catch (ParserConfigurationException | SAXException e) {
      throw new ProjectExplorerException("Error while parsing XML in Studio One project: " + file.getAbsolutePath(), e);
    } finally {
      // Clean up temporary directory
      if (tempDir != null) {
        try {
          org.apache.commons.io.FileUtils.deleteDirectory(tempDir.toFile());
          log.debug("Cleaned up temporary directory: {}", tempDir);
        } catch (IOException e) {
          log.error("Failed to clean up temporary directory: {}", tempDir, e);
        }
      }
    }
  }

  private void extractMetadata(Document metainfoDoc, XPath xpath, DawProject project) {
    // Extract title - if query fails, keep default name from filename
    try {
      NodeList titleNodes = (NodeList) xpath.compile("//Attribute[@id='Document:Title']/@value")
              .evaluate(metainfoDoc, XPathConstants.NODESET);
      if (titleNodes.getLength() > 0) {
        String title = titleNodes.item(0).getNodeValue();
        if (title != null && !title.isEmpty()) {
          project.setName(title);
        }
      }
    } catch (XPathExpressionException e) {
      log.debug("Error extracting Document:Title from metainfo.xml, using default", e);
    }

    // Extract app full name from Generator - if query fails, use default
    try {
      NodeList generatorNodes = (NodeList) xpath.compile("//Attribute[@id='Document:Generator']/@value")
              .evaluate(metainfoDoc, XPathConstants.NODESET);
      if (generatorNodes.getLength() > 0) {
        String generator = generatorNodes.item(0).getNodeValue();
        if (generator != null && generator.startsWith("Studio One/")) {
          project.setAppFullName(generator);
        } else {
          project.setAppFullName("Studio One");
        }
      } else {
        project.setAppFullName("Studio One");
      }
    } catch (XPathExpressionException e) {
      log.debug("Error extracting Document:Generator from metainfo.xml, using default", e);
      project.setAppFullName("Studio One");
    }

    // Extract format version - if query fails, leave unset
    try {
      NodeList versionNodes = (NodeList) xpath.compile("//Attribute[@id='Document:FormatVersion']/@value")
              .evaluate(metainfoDoc, XPathConstants.NODESET);
      if (versionNodes.getLength() > 0) {
        String version = versionNodes.item(0).getNodeValue();
        project.setFormatVersion(version);
      }
    } catch (XPathExpressionException e) {
      log.debug("Error extracting Document:FormatVersion from metainfo.xml, leaving unset", e);
    }
  }

  private void collectPlugins(Path tempDir, DawProject project) {
    // Parse audiomixer.xml for audio effect plugins
    File audiomixerFile = new File(tempDir.toFile(), "Devices/audiomixer.xml");
    if (audiomixerFile.exists()) {
      try {
        Document audiomixerDoc = createDocument(audiomixerFile);
        StudioOneAudioMixerPluginCollector audioCollector = new StudioOneAudioMixerPluginCollector(audiomixerDoc);
        List<DawPlugin> audioPlugins = audioCollector.collectPlugins();
        for (DawPlugin plugin : audioPlugins) {
          plugin.setProject(project);
          project.getPlugins().add(plugin);
        }
      } catch (Exception e) {
        log.error("Error parsing audiomixer.xml", e);
      }
    }

    // Parse audiosynthfolder.xml for instrument plugins
    File synthFile = new File(tempDir.toFile(), "Devices/audiosynthfolder.xml");
    if (synthFile.exists()) {
      try {
        Document synthDoc = createDocument(synthFile);
        StudioOneSynthPluginCollector synthCollector = new StudioOneSynthPluginCollector(synthDoc);
        List<DawPlugin> synthPlugins = synthCollector.collectPlugins();
        for (DawPlugin plugin : synthPlugins) {
          plugin.setProject(project);
          project.getPlugins().add(plugin);
        }
      } catch (Exception e) {
        log.error("Error parsing audiosynthfolder.xml", e);
      }
    }
  }

  private Document createDocument(File file)
      throws ProjectExplorerException, ParserConfigurationException, SAXException, IOException {
    try (FileInputStream fis = new FileInputStream(file)) {
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = builderFactory.newDocumentBuilder();
      return builder.parse(fis);
    } catch (FileNotFoundException e) {
      throw new ProjectExplorerException("File not found: " + file.getAbsolutePath(), e);
    }
  }

}

