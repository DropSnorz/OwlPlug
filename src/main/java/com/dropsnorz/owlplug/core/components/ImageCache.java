package com.dropsnorz.owlplug.core.components;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageCache {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private CacheManager cacheManager;

  ImageCache() {

  }

  /**
   * Retrieve or persist asynchronously an image in cache from url.
   * 
   * @param url Image url
   * @return The created image
   */
  public Image get(String url) {
    return get(url, "png");
  }

  /**
   * Retrieve or persist asynchronously an image in cache from url.
   * 
   * @param url  Image url
   * @param type Image type. Must be png or jpeg.
   * @return The created image
   */
  public Image get(String url, String type) {
    return get(url, type, true);
  }

  /**
   * Retrieve or persist image in cache from url.
   * 
   * @param url        Image url
   * @param type       Image type. Must be png or jpeg.
   * @param asyncFetch true indicates whether the image is being loaded in the
   *                   background
   * @return The created image
   */
  public Image get(String url, String type, boolean asyncFetch) {

    if (url == null || url.isEmpty()) {
      return null;
    }
    Cache<String, byte[]> cache = getCache();
    byte[] cachedElement = cache.get(url);

    // Retrieve image from cache
    if (cachedElement != null) {
      try {
        log.debug("Retrieving image {} from cache", url);
        byte[] content = cachedElement;
        ByteArrayInputStream s = new ByteArrayInputStream(content);
        BufferedImage bufImage = ImageIO.read(s);
        return SwingFXUtils.toFXImage(bufImage, null);
      } catch (IOException e) {
        log.error("Error retrieving image from cache", e);
      }
    }

    // Load image
    Image cachedImage = new Image(url, asyncFetch);
    // In case of async fetch, persist to the cache on complete
    cachedImage.progressProperty().addListener((observable, oldValue, progress) -> {
      if ((Double) progress == 1.0 && !cachedImage.isError()) {
        persistImageIntoCache(cache, url, cachedImage, type);
      }
    });
    // In case of sync fetch, persist image in cache immediately
    if (!asyncFetch && !cachedImage.isError()) {
      persistImageIntoCache(cache, url, cachedImage, type);

    }

    return cachedImage;
  }

  /**
   * Load asynchronously an Image from cache on the given ImageView. If image
   * don't exists in cache, it will be created retrieving the image from url.
   * 
   * @param url       Image url
   * @param imageView Target image view
   */
  public void loadAsync(String url, ImageView imageView) {

    Task<Image> task = new Task<Image>() {
      public Image call() {
        return ImageCache.this.get(url, "png", false);
      }
    };

    task.setOnSucceeded(e -> {
      Image image = task.getValue();
      if (image != null && !image.isError()) {
        Platform.runLater(() -> {
          imageView.setImage(task.getValue());
        });
      }
    });
    new Thread(task).start();

  }

  /**
   * Returns true if cache contains data for the given key.
   * 
   * @param key to look for
   * @return true if cache contains key
   */
  public boolean contains(String key) {
    return getCache().containsKey(key);

  }

  /**
   * Clear image cache contents.
   */
  public void clear() {
    getCache().clear();

  }

  private Cache<String, byte[]> getCache() {
    return cacheManager.getCache("image-cache", String.class, byte[].class);

  }

  private void persistImageIntoCache(Cache<String, byte[]> cache, String key, Image image, String type) {
    try {
      log.debug("Persisting image {} into cache", key);
      BufferedImage buffImage = SwingFXUtils.fromFXImage(image, null);
      ByteArrayOutputStream s = new ByteArrayOutputStream();
      ImageIO.write(buffImage, type, s);
      byte[] res = s.toByteArray();
      s.close(); // especially if you are using a different output stream.
      cache.put(key, res);
    } catch (IllegalArgumentException | IllegalStateException | IOException e) {
      log.error("Error caching image", e);
    }
  }
}
