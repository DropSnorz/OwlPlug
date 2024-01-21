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

package com.owlplug.project.tasks.discovery.reaper;

import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.parsers.reaper.PluginNodeListener;
import com.owlplug.parsers.reaper.ReaperPlugin;
import com.owlplug.parsers.reaper.ReaperProjectLexer;
import com.owlplug.parsers.reaper.ReaperProjectParser;
import com.owlplug.project.model.DawApplication;
import com.owlplug.project.model.DawPlugin;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.tasks.discovery.ProjectExplorer;
import com.owlplug.project.tasks.discovery.ProjectExplorerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FilenameUtils;

public class ReaperProjectExplorer implements ProjectExplorer {

  @Override
  public boolean canExploreFile(File file) {
    return file.isFile() && file.getAbsolutePath().endsWith(".rpp");
  }


  @Override
  public DawProject explore(File file) throws ProjectExplorerException {

    DawProject project = new DawProject();
    project.setApplication(DawApplication.REAPER);
    project.setPath(FileUtils.convertPath(file.getAbsolutePath()));
    project.setName(FilenameUtils.removeExtension(file.getName()));

    try {

      project.setLastModifiedAt(new Date(file.lastModified()));
      BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      FileTime fileTime = attr.creationTime();
      project.setCreatedAt(Date.from(fileTime.toInstant()));

      InputStream is = new FileInputStream(file);

      ReaperProjectLexer lexer = new ReaperProjectLexer(CharStreams.fromStream(is));
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      ReaperProjectParser parser = new ReaperProjectParser(tokens);
      ParseTree pt = parser.node();

      ParseTreeWalker walker = new ParseTreeWalker();
      PluginNodeListener pluginListener = new PluginNodeListener();
      walker.walk(pluginListener, pt);

      for (ReaperPlugin reaperPlugin : pluginListener.getReaperPlugins()) {
        DawPlugin dawPlugin = new DawPlugin();
        dawPlugin.setProject(project);
        dawPlugin.setName(FilenameUtils.removeExtension(reaperPlugin.getFilename()));
        dawPlugin.setPath(reaperPlugin.getFilename());

        if (reaperPlugin.getName().contains("VST3i")) {
          dawPlugin.setFormat(PluginFormat.VST3);
        } else {
          dawPlugin.setFormat(PluginFormat.VST2);
        }
        project.getPlugins().add(dawPlugin);
      }

      return project;

    } catch (IOException e) {
      throw new ProjectExplorerException("Error while opening Reaper project " + file.getAbsolutePath(), e);
    }

  }
}
