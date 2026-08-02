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

import com.owlplug.core.ui.SideBar;
import com.owlplug.project.components.ProjectFilterState;
import com.owlplug.project.model.DawApplication;
import com.owlplug.project.model.DawProject;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectFilterController {

  @Autowired
  private ProjectFilterState filterState;

  private SideBar sideBar;
  private VBox typeSection;
  private final Map<DawApplication, CheckBox> typeCheckBoxes = new EnumMap<>(DawApplication.class);

  @PostConstruct
  public void setup() {
    typeSection = new VBox(4);

    for (DawApplication application : DawApplication.values()) {
      CheckBox cb = new CheckBox(application.getName());
      cb.selectedProperty().addListener((obs, old, selected) -> {
        if (selected != filterState.getSelectedApplications().contains(application)) {
          if (selected) {
            filterState.getSelectedApplications().add(application);
          } else {
            filterState.getSelectedApplications().remove(application);
          }
        }
      });
      filterState.getSelectedApplications().addListener((SetChangeListener<DawApplication>) change ->
          cb.setSelected(filterState.getSelectedApplications().contains(application)));
      typeCheckBoxes.put(application, cb);
      typeSection.getChildren().add(cb);
    }

    ScrollPane scrollPane = new ScrollPane(buildContent());
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.getStyleClass().add("plugin-filter-scroll");

    sideBar = new SideBar(220, scrollPane);
    sideBar.getStyleClass().add("plugin-filter-sidebar");
    sideBar.setVisible(false);
    sideBar.setPrefWidth(0);
  }

  private VBox buildContent() {
    VBox content = new VBox(8);
    content.setPadding(new Insets(12));

    HBox header = new HBox();
    header.setAlignment(Pos.CENTER_LEFT);
    Label titleLabel = new Label("Filters");
    titleLabel.getStyleClass().add("plugin-filter-title");
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    Hyperlink clearLink = new Hyperlink("Clear all");
    clearLink.getStyleClass().add("plugin-filter-clear");
    clearLink.setOnAction(e -> filterState.clearAll());
    header.getChildren().addAll(titleLabel, spacer, clearLink);

    Label typeLabel = new Label("Type");
    typeLabel.getStyleClass().add("plugin-filter-section-title");

    content.getChildren().addAll(header, new Separator(), typeLabel, typeSection);
    return content;
  }

  /**
   * Refreshes filter section counts from the currently displayed projects.
   * Called after each project sync or data reload.
   */
  public void refresh(Iterable<DawProject> projects) {
    Map<DawApplication, Long> counts = new EnumMap<>(DawApplication.class);
    for (DawProject project : projects) {
      counts.merge(project.getApplication(), 1L, Long::sum);
    }
    typeCheckBoxes.forEach((application, cb) ->
        cb.setText(application.getName() + " (" + counts.getOrDefault(application, 0L) + ")"));
  }

  public SideBar getView() {
    return sideBar;
  }
}
