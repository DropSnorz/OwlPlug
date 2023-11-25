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

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.controllers.dialogs.ListDirectoryDialogController;
import com.owlplug.core.ui.FilterableTreeItem;
import com.owlplug.project.components.ProjectTaskFactory;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.services.ProjectService;
import com.owlplug.project.ui.ProjectTreeCell;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectsController extends BaseController {

  @Autowired
  private ProjectService projectService;
  @Autowired
  private ProjectTaskFactory projectTaskFactory;
  @Autowired
  private ListDirectoryDialogController listDirectoryDialogController;
  @Autowired
  private ProjectInfoController projectInfoController;

  @FXML
  private Button syncProjectButton;
  @FXML
  private Button projectDirectoryButton;
  @FXML
  private TextField searchTextField;
  @FXML
  private TabPane projectTreeViewTabPane;
  @FXML
  private TreeView projectTreeView;

  private FilterableTreeItem<Object> projectTreeNode;

  @FXML
  public void initialize() {
    syncProjectButton.setOnAction(e -> {
      projectService.syncProjects();
    });

    projectTaskFactory.addSyncProjectsListener(() -> {
      refresh();
    });

    projectTreeViewTabPane.getStyleClass().add(JMetroStyleClass.UNDERLINE_TAB_PANE);

    projectTreeView.setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
      @Override
      public TreeCell<Object> call(TreeView<Object> p) {
        return new ProjectTreeCell(getApplicationDefaults());
      }
    });

    projectTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue instanceof TreeItem treeItem
           && treeItem.getValue() instanceof DawProject project) {
        projectInfoController.setProject(project);
      }
    });

    projectDirectoryButton.setOnAction(e -> {
      listDirectoryDialogController.configure(ApplicationDefaults.PROJECT_DIRECTORY_KEY);
      listDirectoryDialogController.show();
    });

    projectTreeNode = new FilterableTreeItem<>("(all)");
    projectTreeView.setRoot(projectTreeNode);

    // Binds search property to plugin tree filter
    projectTreeNode.predicateProperty().bind(Bindings.createObjectBinding(() -> {
      if (searchTextField.getText() == null || searchTextField.getText().isEmpty()) {
        return null;
      }
      return (item) -> {
        if (item instanceof DawProject project) {
          return project.getName().toLowerCase().contains(searchTextField.getText().toLowerCase());
        } else {
          return item.toString().toLowerCase().contains(searchTextField.getText().toLowerCase());
        }
      };
    }, searchTextField.textProperty()));


    refresh();

  }

  public void refresh() {
    Iterable<DawProject> projects = projectService.getAllProjects();
    projectTreeNode.getInternalChildren().clear();

    for (DawProject p : projects) {
      projectTreeNode.getInternalChildren().add(new FilterableTreeItem<>(p));
    }

    projectTreeNode.setExpanded(true);

  }
}
