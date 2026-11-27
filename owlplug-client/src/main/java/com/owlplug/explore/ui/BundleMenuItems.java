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

import com.owlplug.core.utils.StringUtils;
import com.owlplug.explore.controllers.ExploreController;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import java.util.List;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Builds the "Other Packages" context menu entries of a remote package, one per bundle.
 */
public final class BundleMenuItems {

  private BundleMenuItems() {
  }

  /**
   * Replaces the menu items with the bundles currently visible for the package. Meant to be
   * called when the context menu opens, since visible bundles depend on the Explore filters.
   */
  public static void fill(Menu menu, RemotePackage remotePackage, ExploreController parentController) {
    List<PackageBundle> bundles = parentController.getVisibleBundles(remotePackage);
    menu.getItems().clear();
    for (PackageBundle bundle : bundles) {
      menu.getItems().add(build(bundle, parentController));
    }
    menu.setDisable(bundles.isEmpty());
  }

  private static MenuItem build(PackageBundle bundle, ExploreController parentController) {
    TextFlow bundleTextFlow = new TextFlow();
    bundleTextFlow.getChildren().add(new Label(bundle.isInstaller() ? "Run installer" : "Install"));
    Text bundleSource = new Text(" (" + StringUtils.truncate(bundle.getName(), 50, "...") + ")");
    bundleSource.getStyleClass().add("text-disabled");
    bundleTextFlow.getChildren().add(bundleSource);
    CustomMenuItem bundleMenuItem = new CustomMenuItem(bundleTextFlow);
    bundleMenuItem.setOnAction(e -> parentController.installBundle(bundle));
    return bundleMenuItem;
  }
}
