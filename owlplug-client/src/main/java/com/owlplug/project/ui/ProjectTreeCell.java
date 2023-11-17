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
import com.owlplug.project.model.LookupResult;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.model.DawPlugin;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ProjectTreeCell extends TreeCell<Object> {

  private ApplicationDefaults applicationDefaults;

  public ProjectTreeCell(ApplicationDefaults applicationDefaults) {
    this.applicationDefaults = applicationDefaults;
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
        ImageView icon = new ImageView(applicationDefaults.getDAWApplicationIcon(project.getApplication()));
        hbox.getChildren().add(icon);
        Label label = new Label(project.getName());
        hbox.getChildren().add(label);
        List<DawPlugin> failedLookups = project.getPluginByLookupResult(LookupResult.MISSING);
        if (!failedLookups.isEmpty()) {
          Label missingLabel = new Label(failedLookups.size() + " Missing plugin(s)");
          missingLabel.setGraphic(new ImageView(applicationDefaults.errorIconImage));
          missingLabel.getStyleClass().add("label-danger");
          hbox.getChildren().add(missingLabel);
        }

        setGraphic(hbox);
      } else {
        setText(item.toString());
        setGraphic(getTreeItem().getGraphic());
      }
    }

  }

}
