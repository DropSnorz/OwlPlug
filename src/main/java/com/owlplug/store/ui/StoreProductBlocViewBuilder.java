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
