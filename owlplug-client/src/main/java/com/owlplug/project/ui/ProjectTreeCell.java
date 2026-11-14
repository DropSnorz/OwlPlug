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

package com.owlplug.project.ui;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.services.LabelService;
import com.owlplug.core.ui.LabelBadgeView;
import com.owlplug.project.model.DawPlugin;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.model.LookupResult;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class ProjectTreeCell extends TreeCell<Object> {

  private ApplicationDefaults applicationDefaults;
  private LabelService labelService;

  public ProjectTreeCell(ApplicationDefaults applicationDefaults, LabelService labelService) {
    this.applicationDefaults = applicationDefaults;
    this.labelService = labelService;
  }

  @Override
  public void updateItem(Object item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null) {
      setText(null);
      setGraphic(null);
    } else {
      if (item instanceof DawProject project) {
        setText(null);
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        ImageView icon = new ImageView(applicationDefaults.getDAWApplicationIcon(project.getApplication()));
        hbox.getChildren().add(icon);
        Label label = new Label(project.getName());
        hbox.getChildren().add(label);
        List<DawPlugin> failedLookups = project.getPluginByLookupResult(LookupResult.MISSING);
        if (!failedLookups.isEmpty()) {
          Label missingLabel = new Label(failedLookups.size() + " Missing plugin(s)");
          FontIcon statusIcon = new FontIcon("mdi2a-alert-circle-outline");
          statusIcon.getStyleClass().add("status-icon-missing");
          missingLabel.setGraphic(statusIcon);
          missingLabel.getStyleClass().add("label-danger");
          hbox.getChildren().add(missingLabel);
        }
        if (project.isBackup()) {
          Label backupLabel = new Label("Backup");
          FontIcon backupIcon = new FontIcon("mdi2h-history");
          backupIcon.getStyleClass().add("status-icon-backup");
          backupLabel.setGraphic(backupIcon);
          backupLabel.getStyleClass().add("label-warning");
          hbox.getChildren().add(backupLabel);
        }
        if (labelService.isShowInLists() && project.getFootprint() != null) {
          // Grouped in their own container so badges are spaced like in the plugin list (4px),
          // independently of the wider spacing between the other cell elements
          HBox labelsBox = new HBox(4);
          labelsBox.setAlignment(Pos.CENTER_LEFT);
          labelsBox.getChildren().addAll(LabelBadgeView.of(project.getFootprint().getLabels()));
          hbox.getChildren().add(labelsBox);
        }

        setGraphic(hbox);
      } else {
        setText(item.toString());
        setGraphic(getTreeItem().getGraphic());
      }
    }

  }

}
