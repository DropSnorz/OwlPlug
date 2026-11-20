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

import com.owlplug.core.utils.FileUtils;
import com.owlplug.project.model.DawApplication;
import com.owlplug.project.model.DawPlugin;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.tasks.discovery.ProjectExplorer;
import com.owlplug.project.tasks.discovery.ProjectExplorerException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.FilenameUtils;

public class DawprojectProjectExplorer implements ProjectExplorer {

  private static final long MAX_XML_SIZE = 128L * 1024 * 1024;

  @Override
  public boolean canExploreFile(File file) {
    return file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".dawproject");
  }

  @Override
  public DawProject explore(File file) throws ProjectExplorerException {
    if (!canExploreFile(file)) {
      return null;
    }

    try (ZipFile archive = new ZipFile(file)) {
      ZipEntry projectEntry = archive.getEntry("project.xml");
      if (projectEntry == null) {
        throw new ProjectExplorerException("Missing project.xml in " + file.getAbsolutePath(), null);
      }

      DawprojectXmlReader reader = new DawprojectXmlReader();
      DawprojectXmlReader.ProjectContent content;
      try (InputStream input = xmlInput(archive, projectEntry)) {
        content = reader.readProject(input);
      }

      String name = FilenameUtils.removeExtension(file.getName());
      ZipEntry metadataEntry = archive.getEntry("metadata.xml");
      if (metadataEntry != null) {
        try (InputStream input = xmlInput(archive, metadataEntry)) {
          String title = reader.readTitle(input);
          if (title != null && !title.isBlank()) {
            name = title.strip();
          }
        }
      }

      DawProject project = new DawProject();
      project.setApplication(DawApplication.DAWPROJECT);
      project.setPath(FileUtils.convertPath(file.getAbsolutePath()));
      project.setName(name);
      project.setFormatVersion(content.version());

      String appName = content.applicationName();
      if (appName == null || appName.isBlank()) {
        project.setAppFullName("DAWproject");
      } else {
        String appVersion = content.applicationVersion();
        project.setAppFullName(appName + (appVersion == null || appVersion.isBlank()
            ? "" : " " + appVersion));
      }

      BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      project.setLastModifiedAt(new Date(file.lastModified()));
      project.setCreatedAt(Date.from(attributes.creationTime().toInstant()));

      for (DawPlugin plugin : content.plugins()) {
        plugin.setProject(project);
        project.getPlugins().add(plugin);
      }
      return project;

    } catch (IOException | XMLStreamException e) {
      throw new ProjectExplorerException("Error reading DAWproject file: " + file.getAbsolutePath(), e);
    }
  }

  private InputStream xmlInput(ZipFile archive, ZipEntry entry) throws IOException {
    if (entry.getSize() < 0 || entry.getSize() > MAX_XML_SIZE) {
      throw new IOException("Unsupported DAWproject XML size: " + entry.getName());
    }
    return archive.getInputStream(entry);
  }
}
