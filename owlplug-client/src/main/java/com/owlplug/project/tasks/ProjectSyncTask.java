package com.owlplug.project.tasks;

import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskResult;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.project.dao.ProjectDAO;
import com.owlplug.project.model.Project;
import com.owlplug.project.model.ProjectPlugin;
import com.owlplug.project.tasks.discovery.AbletonProjectExplorer;
import java.io.File;
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

    for (String directory : projectDirectories) {
      File dir = new File(directory);
      this.updateMessage("Syncing projects from: " + dir.getAbsolutePath());


      if (dir.isDirectory()) {
        List<File> baseFiles = (List<File>) FileUtils.listUniqueFilesAndDirs(dir);

        for (File file : baseFiles) {
          this.updateMessage("Analyzing file: " + file.getAbsolutePath());
          AbletonProjectExplorer explorer = new AbletonProjectExplorer();

          if (explorer.canExploreFile(file)) {
            Project project = explorer.explore(file);
            projectDAO.save(project);
          }
        }
      }
    }

    this.updateMessage("All projects are synchronized");
    this.updateProgress(1,1);

    return success();
  }
}
