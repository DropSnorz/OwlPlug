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
import com.owlplug.core.controllers.MainController;
import com.owlplug.core.controllers.SettingsController;
import com.owlplug.core.ui.FilterableTreeItem;
import com.owlplug.core.utils.FX;
import com.owlplug.project.components.ProjectFilterState;
import com.owlplug.project.events.ProjectSyncEvent;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.services.ProjectService;
import com.owlplug.project.ui.ProjectTreeCell;
import java.util.function.Predicate;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectsController extends BaseController {

  @Autowired
  private ProjectService projectService;
  @Autowired
  private MainController mainController;
  @Autowired
  private SettingsController settingsController;
  @Autowired
  private ProjectInfoController projectInfoController;
  @Autowired
  private ProjectFilterController filterController;
  @Autowired
  private ProjectFilterState filterState;

  @FXML
  private Button syncProjectButton;
  @FXML
  private Button projectDirectoryButton;
  @FXML
  private Button filterToggleButton;
  @FXML
  private TextField searchTextField;
  @FXML
  private TabPane projectTreeViewTabPane;
  @FXML
  private TreeView projectTreeView;
  @FXML
  private HBox filterContainer;

  private FilterableTreeItem<Object> projectTreeNode;

  @FXML
  public void initialize() {
    syncProjectButton.setOnAction(e -> {
      this.getTelemetryService().event("/Projects/Scan");
      projectService.syncProjects();
    });

    projectTreeView.setCellFactory((Callback<TreeView<Object>, TreeCell<Object>>)
                                       p -> new ProjectTreeCell(getApplicationDefaults()));

    projectTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue instanceof TreeItem treeItem
           && treeItem.getValue() instanceof DawProject project) {
        projectInfoController.projectProperty().set(project);
      }
    });

    projectDirectoryButton.setOnAction(e -> {
      settingsController.navigateToSection(SettingsController.PROJECTS_SECTION_INDEX);
      mainController.navigateToMainTab(MainController.SETTINGS_TAB_INDEX);
    });

    // Wire filter sidebar into the scene graph
    filterContainer.getChildren().add(0, filterController.getView());

    projectTreeNode = new FilterableTreeItem<>("(all)");
    projectTreeView.setRoot(projectTreeNode);

    // Binds search text and type filter to the project tree predicate
    projectTreeNode.predicateProperty().bind(Bindings.createObjectBinding(
        () -> buildTreePredicate(searchTextField.getText(), filterState.predicateProperty().get()),
        searchTextField.textProperty(), filterState.predicateProperty()));

    refresh();

  }

  private Predicate<Object> buildTreePredicate(String searchVal, Predicate<DawProject> filterPredicate) {
    boolean hasSearch = searchVal != null && !searchVal.isEmpty();
    if (!hasSearch && filterPredicate == null) {
      return null;
    }
    return (item) -> {
      if (item instanceof DawProject project) {
        boolean searchMatch = !hasSearch
            || project.getName().toLowerCase().contains(searchVal.toLowerCase());
        return searchMatch && (filterPredicate == null || filterPredicate.test(project));
      } else {
        return !hasSearch || item.toString().toLowerCase().contains(searchVal.toLowerCase());
      }
    };
  }

  public void refresh() {
    Iterable<DawProject> projects = projectService.getAllProjects();
    projectTreeNode.getInternalChildren().clear();

    for (DawProject p : projects) {
      projectTreeNode.getInternalChildren().add(new FilterableTreeItem<>(p));
    }

    projectTreeNode.setExpanded(true);
    filterController.refresh(projects);

  }

  @FXML
  public void toggleFilter() {
    filterController.getView().toggle();
  }

  @EventListener
  private void handle(ProjectSyncEvent event) {
    FX.run(this::refresh);
  }
}
