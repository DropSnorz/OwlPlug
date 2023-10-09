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

package com.owlplug.project.controllers;

import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.ui.FilterableTreeItem;
import com.owlplug.project.components.ProjectTaskFactory;
import com.owlplug.project.model.Project;
import com.owlplug.project.services.ProjectService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectsController extends BaseController {

  @Autowired
  private ProjectService projectService;
  @Autowired
  private ProjectTaskFactory projectTaskFactory;
  @FXML
  private Button syncProjectButton;
  @FXML
  private TabPane projectTreeViewTabPane;
  @FXML
  private TreeView projectTreeView;

  @FXML
  public void initialize() {
    syncProjectButton.setOnAction(e -> {
      projectService.syncProjects();
    });

    projectTaskFactory.addSyncPluginsListener(() -> {
      refresh();
    });

    projectTreeViewTabPane.getStyleClass().add(JMetroStyleClass.UNDERLINE_TAB_PANE);

    refresh();
  }

  public void refresh() {
    Iterable<Project> projects = projectService.getAllProjects();
    FilterableTreeItem<Object> root = new FilterableTreeItem<>("(all)");
    projectTreeView.setRoot(root);

    for (Project p : projects) {
      root.getInternalChildren().add(new FilterableTreeItem<>(p.getName()));
    }

  }
}
