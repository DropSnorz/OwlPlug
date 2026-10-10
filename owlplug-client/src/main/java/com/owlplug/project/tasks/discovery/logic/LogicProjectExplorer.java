package com.owlplug.project.tasks.discovery.logic;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.project.model.DawApplication;
import com.owlplug.project.model.DawPlugin;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.tasks.discovery.ProjectExplorer;
import com.owlplug.project.tasks.discovery.ProjectExplorerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class LogicProjectExplorer implements ProjectExplorer {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public boolean canExploreFile(File file) {
    return file.isDirectory() && file.getName().toLowerCase(Locale.ROOT).endsWith(".logicx");
  }

  @Override
  public DawProject explore(File file) throws ProjectExplorerException {
    if (!canExploreFile(file)) {
      return null;
    }

    File alternative = findProjectDataAlternative(file);
    File projectDataFile = new File(alternative, "ProjectData");

    try {
      byte[] projectData = Files.readAllBytes(projectDataFile.toPath());
      Map<String, LogicAuReference> uniqueReferences = new LinkedHashMap<>();
      LogicAuReferenceExtractor extractor = new LogicAuReferenceExtractor();
      for (LogicAuReference reference : extractor.extractFromActiveUserTracks(projectData)) {
        uniqueReferences.putIfAbsent(reference.fingerprint(), reference);
      }

      DawProject project = new DawProject();
      project.setApplication(DawApplication.LOGIC);
      project.setPath(FileUtils.convertPath(file.getAbsolutePath()));
      project.setName(FilenameUtils.removeExtension(file.getName()));
      project.setAppFullName(readLogicVersion(file));

      BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      project.setCreatedAt(Date.from(attributes.creationTime().toInstant()));
      project.setLastModifiedAt(new Date(projectDataFile.lastModified()));

      for (LogicAuReference reference : uniqueReferences.values()) {
        DawPlugin dawPlugin = new DawPlugin();
        dawPlugin.setProject(project);
        dawPlugin.setName(reference.displayName());
        dawPlugin.setUid(reference.fingerprint());
        dawPlugin.setFormat(PluginFormat.AU);
        project.getPlugins().add(dawPlugin);
      }

      return project;
    } catch (IOException e) {
      throw new ProjectExplorerException("Error while reading Logic project: " + file.getAbsolutePath(), e);
    }
  }

  private File findProjectDataAlternative(File projectBundle) throws ProjectExplorerException {
    File alternativesDirectory = new File(projectBundle, "Alternatives");
    File[] alternatives = alternativesDirectory.listFiles(File::isDirectory);
    if (alternatives == null) {
      throw new ProjectExplorerException("Logic project has no Alternatives directory: "
          + projectBundle.getAbsolutePath(), null);
    }

    Arrays.sort(alternatives, (left, right) -> left.getName().compareToIgnoreCase(right.getName()));
    for (File alternative : alternatives) {
      if (new File(alternative, "ProjectData").isFile()) {
        return alternative;
      }
    }

    throw new ProjectExplorerException("Logic project has no ProjectData file: "
        + projectBundle.getAbsolutePath(), null);
  }

  private String readLogicVersion(File projectBundle) throws ProjectExplorerException {
    File projectInformation = new File(projectBundle, "Resources/ProjectInformation.plist");
    if (!projectInformation.isFile()) {
      return "Logic Pro";
    }

    try {
      NSObject rootObject = PropertyListParser.parse(projectInformation);
      if (rootObject instanceof NSDictionary projectInformationDictionary) {
        NSObject lastSavedFrom = projectInformationDictionary.objectForKey("LastSavedFrom");
        if (lastSavedFrom != null && !lastSavedFrom.toString().isBlank()) {
          return lastSavedFrom.toString();
        }
      }
      return "Logic Pro";
    } catch (IOException | PropertyListFormatException | java.text.ParseException
             | ParserConfigurationException | SAXException e) {
      log.warn("Could not read Logic project information: {}", projectInformation.getAbsolutePath(), e);
      return "Logic Pro";
    }
  }
}