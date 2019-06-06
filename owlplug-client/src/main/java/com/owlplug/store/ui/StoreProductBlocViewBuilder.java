/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.store.ui;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ImageCache;
import com.owlplug.store.controllers.StoreController;
import com.owlplug.store.model.StoreProduct;
import javafx.scene.image.Image;

public class StoreProductBlocViewBuilder {

  private ApplicationDefaults applicationDefaults;
  private ImageCache imageCache;
  private StoreController storeController;

  /**
   * Creates a new builder instance.
   * 
   * @param applicationDefaults - OwlPlug application defaults
   * @param imageCache          - OwlPlug image cache
   * @param storeController     - parent store controller
   */
  public StoreProductBlocViewBuilder(ApplicationDefaults applicationDefaults, ImageCache imageCache,
      StoreController storeController) {
    super();
    this.applicationDefaults = applicationDefaults;
    this.imageCache = imageCache;
    this.storeController = storeController;
  }

  /**
   * Build a new {@link StoreProductBlocView} instance.
   * 
   * @param storeProduct - Related store product
   * @return A {@link StoreProductBlocView} instance.
   */
  public StoreProductBlocView build(StoreProduct storeProduct) {

    Image image = imageCache.get(storeProduct.getScreenshotUrl());
    return new StoreProductBlocView(applicationDefaults, storeProduct, image, storeController);
  }

}
