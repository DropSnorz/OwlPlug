package com.owlplug.project.tasks;

import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskResult;
import com.owlplug.project.model.Project;
import com.owlplug.project.model.ProjectPlugin;
import com.owlplug.project.tasks.discovery.AbletonProjectExplorer;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectSyncTask extends AbstractTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  protected TaskResult call() throws Exception {

    log.debug("Starting project sync task");

    String path = "C:/Users/arthu/Blend/Lone Wanderer Project/DropSnorz - Lone Wanderer.als";

    File sourceFile = new File(path);

    AbletonProjectExplorer explorer = new AbletonProjectExplorer();
    Project project = explorer.explore(sourceFile);

    for (ProjectPlugin p : project.getPlugins()) {
      log.debug(p.getName() + " - " + p.getFileName());
    }

    return success();
  }
}
