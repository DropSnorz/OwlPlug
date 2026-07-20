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

package com.owlplug.core.ui;

import com.owlplug.core.components.ImageCache;
import com.owlplug.core.utils.FX;
import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * An {@link ImageView} substitute for virtualized lists (e.g. a ListView cell factory). Never
 * resolves an image inline on the calling thread: an image already resident in
 * {@link ImageCache}'s memory tier is attached immediately, otherwise a placeholder is shown
 * and the image is resolved off-thread via {@link ImageCache#getAsync(String)}, swapped in once
 * ready.
 */
public class VirtualizedImageView extends StackPane {

  private final ImageCache imageCache;
  private final ImageView imageView;
  private final Region loadingPane;

  private String currentUrl;

  public VirtualizedImageView(ImageCache imageCache, double width, double height) {
    this.imageCache = imageCache;
    this.setPrefSize(width, height);
    this.getStyleClass().add("virtualized-image-view");

    loadingPane = new Region();
    loadingPane.getStyleClass().add("image-loading-pane");
    loadingPane.setPrefSize(width, height);

    imageView = new ImageView();
    imageView.setFitWidth(width);
    imageView.setFitHeight(height);
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);

    this.getChildren().addAll(loadingPane, imageView);
  }

  /**
   * Binds this view to the given image url. Safe to call repeatedly on the same instance (e.g.
   * a recycled cell) — a call for a url already in-flight or displayed is a no-op, and a stale
   * in-flight lookup for a previously-bound url is discarded once it resolves.
   *
   * @param url Image url, may be null/empty to clear the view
   */
  public void setImageUrl(String url) {
    if (Objects.equals(url, currentUrl)) {
      return;
    }
    currentUrl = url;
    imageView.setImage(null);

    if (url == null || url.isEmpty()) {
      setLoading(false);
      return;
    }

    Image memoryImage = imageCache.getFromMemoryCache(url);
    if (memoryImage != null) {
      setLoading(false);
      imageView.setImage(memoryImage);
      return;
    }

    setLoading(true);
    imageCache.getAsync(url).thenAccept(image -> FX.run(() -> {
      // Discard if this view has since been rebound to a different url.
      if (!url.equals(currentUrl)) {
        return;
      }
      if (image != null && !image.isError()) {
        imageView.setImage(image);
      }
      setLoading(false);
    }));
  }

  private void setLoading(boolean loading) {
    loadingPane.setVisible(loading);
  }

}
