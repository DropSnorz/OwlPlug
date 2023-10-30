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
import com.owlplug.project.model.Project;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;

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
      if (item instanceof Project project) {
        // Spaces can be replaced by a container margin
        setText("   " + project.getName());
        setGraphic(new ImageView(applicationDefaults.getDAWApplicationIcon(project.getApplication())));
      } else {
        setText(item.toString());
        setGraphic(getTreeItem().getGraphic());
      }
    }

  }

}
