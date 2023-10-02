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

import com.owlplug.core.components.BaseTaskFactory;
import com.owlplug.core.tasks.TaskExecutionContext;
import com.owlplug.project.tasks.ProjectSyncTask;
import org.springframework.stereotype.Component;

@Component
public class ProjectTaskFactory extends BaseTaskFactory {

  public TaskExecutionContext createSyncTask() {
    return create(new ProjectSyncTask());
  }
}
