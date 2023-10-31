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

package com.owlplug.project.tasks;

import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskResult;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.project.dao.ProjectDAO;
import com.owlplug.project.model.Project;
import com.owlplug.project.tasks.discovery.ableton.AbletonProjectExplorer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectSyncTask extends AbstractTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private ProjectDAO projectDAO;
  private List<String> projectDirectories;

  public ProjectSyncTask(ProjectDAO projectDAO,
                         List<String> projectDirectories) {
    this.projectDAO = projectDAO;
    this.projectDirectories = projectDirectories;
  }


  @Override
  protected TaskResult call() throws Exception {

    log.debug("Starting project sync task");
    this.updateProgress(0,1);

    projectDAO.deleteAll();


    List<File> baseFiles = new ArrayList<>();
    for (String directory : projectDirectories) {
      File dir = new File(directory);
      this.updateMessage("Syncing projects from: " + dir.getAbsolutePath());
      if (dir.isDirectory()) {
        baseFiles.addAll(FileUtils.listUniqueFilesAndDirs(dir));
      }
    }

    this.setMaxProgress(baseFiles.size());

    for (File file : baseFiles) {
      this.commitProgress(1);
      AbletonProjectExplorer explorer = new AbletonProjectExplorer();

      if (explorer.canExploreFile(file)) {
        this.updateMessage("Analyzing file: " + file.getAbsolutePath());
        Project project = explorer.explore(file);
        projectDAO.save(project);
      }
    }

    this.updateMessage("All projects are synchronized");
    this.updateProgress(1,1);

    return success();
  }
}
