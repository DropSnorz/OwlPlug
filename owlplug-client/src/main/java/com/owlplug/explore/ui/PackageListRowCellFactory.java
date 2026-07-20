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

package com.owlplug.explore.ui;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ImageCache;
import com.owlplug.explore.controllers.ExploreController;
import com.owlplug.explore.model.RemotePackage;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class PackageListRowCellFactory implements Callback<ListView<RemotePackage>, ListCell<RemotePackage>> {

  private final ApplicationDefaults applicationDefaults;
  private final ImageCache imageCache;
  private final ExploreController parentController;

  public PackageListRowCellFactory(ApplicationDefaults applicationDefaults, ImageCache imageCache,
      ExploreController parentController) {
    this.applicationDefaults = applicationDefaults;
    this.imageCache = imageCache;
    this.parentController = parentController;
  }

  @Override
  public ListCell<RemotePackage> call(ListView<RemotePackage> listView) {
    return new ListCell<>() {
      @Override
      protected void updateItem(RemotePackage remotePackage, boolean empty) {
        super.updateItem(remotePackage, empty);
        if (empty || remotePackage == null) {
          setGraphic(null);
        } else {
          setGraphic(new PackageListRowView(applicationDefaults, imageCache, remotePackage, parentController));
        }
        // Force re-rendering immediately to avoid blinking cell
        applyCss();
      }
    };
  }

}