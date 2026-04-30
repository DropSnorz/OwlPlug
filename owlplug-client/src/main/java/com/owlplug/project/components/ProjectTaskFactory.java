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


package com.owlplug.project.components;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ApplicationPreferences;
import com.owlplug.core.components.BaseTaskFactory;
import com.owlplug.core.tasks.TaskExecutionContext;
import com.owlplug.project.events.ProjectSyncEvent;
import com.owlplug.project.repositories.DawPluginRepository;
import com.owlplug.project.repositories.DawProjectRepository;
import com.owlplug.project.services.PluginLookupService;
import com.owlplug.project.tasks.PluginLookupTask;
import com.owlplug.project.tasks.ProjectSyncTask;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ProjectTaskFactory extends BaseTaskFactory {

  @Autowired
  private ApplicationPreferences prefs;

  @Autowired
  private PluginLookupService lookupService;
  @Autowired
  private DawProjectRepository projectRepository;
  @Autowired
  private DawPluginRepository dawPluginRepository;
  @Autowired
  private ApplicationEventPublisher publisher;

  public TaskExecutionContext createSyncTask() {

    List<String> directories = prefs.getList(ApplicationDefaults.PROJECT_DIRECTORY_KEY);

    ProjectSyncTask task = new ProjectSyncTask(projectRepository, directories);
    task.setOnSucceeded(e -> {
      createLookupTask().scheduleNow();
      publisher.publishEvent(new ProjectSyncEvent());
    });
    return create(task);
  }

  public TaskExecutionContext createLookupTask() {

    PluginLookupTask task = new PluginLookupTask(dawPluginRepository, lookupService);
    task.setOnSucceeded(e -> {
      publisher.publishEvent(new ProjectSyncEvent());
    });
    return create(task);
  }
}
