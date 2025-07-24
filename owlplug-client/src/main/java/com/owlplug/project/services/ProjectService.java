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

package com.owlplug.project.services;

import com.owlplug.core.services.BaseService;
import com.owlplug.project.components.ProjectTaskFactory;
import com.owlplug.project.repositories.DawProjectRepository;
import com.owlplug.project.model.DawProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService extends BaseService {

  @Autowired
  private DawProjectRepository dawProjectRepository;
  @Autowired
  private ProjectTaskFactory taskFactory;

  public void syncProjects() {
    taskFactory.createSyncTask().schedule();
  }

  public Iterable<DawProject> getAllProjects() {
    return dawProjectRepository.findAll();
  }

}
