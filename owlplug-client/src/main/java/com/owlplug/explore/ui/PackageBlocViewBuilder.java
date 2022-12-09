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
import javafx.scene.image.Image;

public class PackageBlocViewBuilder {

  private ApplicationDefaults applicationDefaults;
  private ImageCache imageCache;
  private ExploreController exploreController;

  /**
   * Creates a new builder instance.
   * 
   * @param applicationDefaults - OwlPlug application defaults
   * @param imageCache          - OwlPlug image cache
   * @param exploreController     - parent store controller
   */
  public PackageBlocViewBuilder(ApplicationDefaults applicationDefaults, ImageCache imageCache,
                                ExploreController exploreController) {
    super();
    this.applicationDefaults = applicationDefaults;
    this.imageCache = imageCache;
    this.exploreController = exploreController;
  }

  /**
   * Build a new {@link PackageBlocView} instance.
   * 
   * @param remotePackage - Related store product
   * @return A {@link PackageBlocView} instance.
   */
  public PackageBlocView build(RemotePackage remotePackage) {

    Image image = imageCache.get(remotePackage.getScreenshotUrl());
    return new PackageBlocView(applicationDefaults, remotePackage, image, exploreController);
  }

}
