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
 
package com.owlplug.core.components;

import com.owlplug.core.utils.Async;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
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

/**
 * Caches images displayed in the UI (e.g. package screenshots) behind three
 * lookup tiers, checked in order:
 *
 * <ol>
 * <li><b>Memory tier</b> ({@code image-memory-cache}) — live, GPU-backed
 * {@link Image} instances. Heap-only, bounded LRU (see {@link #getMemoryCache()}),
 * not persisted across restarts. Reusing the same {@code Image}/texture
 * instance per URL avoids creating/disposing native D3D textures on every UI
 * redraw (e.g. fast scrolling), which otherwise races the JavaFX render
 * thread.</li>
 * <li><b>Disk tier</b> ({@code image-disk-cache}) — encoded image bytes
 * (PNG/JPEG). Heap+disk backed, persists across app restarts, so images
 * don't need to be re-downloaded across sessions. A disk-tier hit is decoded
 * and promoted into the memory tier.</li>
 * <li><b>Network</b> — fetched from the given URL when neither tier has the
 * image. The result is stored in the memory tier immediately, and persisted
 * to the disk tier once fully loaded.</li>
 * </ol>
 *
 * Concurrent lookups for the same URL that both miss the memory tier (e.g. the background
 * {@link #warm(String)} thread racing an on-screen cell factory) are coalesced: only the first
 * caller performs the disk/network lookup, and every other caller for that URL receives the
 * exact same {@link Image} instance instead of racing to create and cache its own.
 */
@Component
public class ImageCache {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private CacheManager cacheManager;

  private final ConcurrentHashMap<String, Image> pendingFetches = new ConcurrentHashMap<>();

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
   * Retrieve or persist image in cache from url, checking the memory tier,
   * then the disk tier, then falling back to the network. See the class
   * Javadoc for details on each tier.
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

    Image memoryImage = lookupMemoryCache(url);
    if (memoryImage != null) {
      return memoryImage;
    }

    // Concurrent misses for the same URL must not each create their own Image instance:
    // ConcurrentHashMap#computeIfAbsent is atomic per key, so only the first caller runs the
    // disk/network lookup below, while every other caller for this URL blocks briefly and
    // receives that exact same instance instead of a separate one that would otherwise lose
    // the race to be the one left in the memory cache (see class Javadoc). Only the thread
    // that actually runs the mapping function (i.e. the first caller) can ever see
    // resolvedImmediately == true, since a piggybacking caller's own local flag is never
    // touched by the other thread's lambda invocation.
    boolean[] resolvedImmediately = new boolean[1];
    Image image = pendingFetches.computeIfAbsent(url, u -> {
      Image diskImage = lookupDiskCache(u);
      if (diskImage != null) {
        resolvedImmediately[0] = true;
        return diskImage;
      }
      Image fetchedImage = fetchFromUrl(u, type, asyncFetch);
      // A disk hit and a synchronous fetch both commit to the memory cache before returning
      // here, so it's safe to stop coalescing this URL immediately. An async network fetch
      // instead returns before it has loaded, so its pendingFetches entry is released later,
      // by fetchFromUrl's own listeners, once the image is actually committed (or has failed).
      resolvedImmediately[0] = !asyncFetch;
      return fetchedImage;
    });
    if (resolvedImmediately[0]) {
      pendingFetches.remove(url, image);
    }
    return image;
  }

  /**
   * Tier 1: look up an already-decoded, GPU-backed {@link Image} instance in
   * the in-memory cache.
   *
   * @param url Image url
   * @return The cached image, or null if not present in the memory tier
   */
  private Image lookupMemoryCache(String url) {
    return getMemoryCache().get(url);
  }

  /**
   * Tier 2: look up encoded image bytes in the persistent disk cache. On hit,
   * the decoded image is promoted into the memory tier so subsequent lookups
   * avoid re-decoding.
   *
   * @param url Image url
   * @return The decoded image, or null if not present in the disk tier
   */
  private Image lookupDiskCache(String url) {
    byte[] cachedElement = getDiskCache().get(url);
    if (cachedElement == null) {
      return null;
    }
    try {
      log.trace("Retrieving image {} from disk cache", url);
      ByteArrayInputStream s = new ByteArrayInputStream(cachedElement);
      BufferedImage bufImage = ImageIO.read(s);
      if (bufImage == null) {
        log.error("Invalid image data in disk cache for {}, evicting entry", url);
        getDiskCache().remove(url);
        return null;
      }
      Image image = SwingFXUtils.toFXImage(bufImage, null);
      getMemoryCache().put(url, image);
      return image;
    } catch (IOException e) {
      log.error("Error retrieving image from disk cache", e);
      return null;
    }
  }

  /**
   * Tier 3: fetch the image from its URL. The image is stored in the memory
   * tier immediately, and persisted to the disk tier once fully loaded
   * (or immediately, for a synchronous fetch).
   *
   * @param url        Image url
   * @param type       Image type. Must be png or jpeg.
   * @param asyncFetch true indicates whether the image is being loaded in the
   *                   background
   * @return The newly created image
   */
  private Image fetchFromUrl(String url, String type, boolean asyncFetch) {
    Image fetchedImage = new Image(url, asyncFetch);

    // In case of async fetch, persist to the disk cache on complete
    fetchedImage.progressProperty().addListener((observable, oldValue, progress) -> {
      if ((Double) progress == 1.0 && !fetchedImage.isError()) {
        Async.runAsync(() -> {
          getMemoryCache().put(url, fetchedImage);
          persistToDiskCache(url, fetchedImage, type);
          pendingFetches.remove(url, fetchedImage);
        });
      }
    });
    // An async fetch that fails never reaches progress 1.0 above, so its pendingFetches
    // entry would otherwise never be released, permanently blocking any retry of this URL.
    fetchedImage.errorProperty().addListener((observable, oldValue, isError) -> {
      if (isError) {
        pendingFetches.remove(url, fetchedImage);
      }
    });
    // In case of sync fetch, persist image in the disk cache immediately
    if (!asyncFetch && !fetchedImage.isError()) {
      getMemoryCache().put(url, fetchedImage);
      persistToDiskCache(url, fetchedImage, type);
    }

    return fetchedImage;
  }

  /**
   * Warms the memory (and disk) tiers for the given url without touching any UI element.
   * Safe to call from a background thread.
   *
   * @param url Image url
   */
  public void warm(String url) {
    get(url, "png", true);
  }

  /**
   * Load asynchronously an Image from cache on the given ImageView. If image
   * don't exist in cache, it will be created retrieving the image from url.
   *
   * @param url       Image url
   * @param imageView Target image view
   */
  public void loadAsync(String url, ImageView imageView) {

    Task<Image> task = new Task<>() {
      public Image call() {
        return ImageCache.this.get(url, "png", false);
      }
    };

    task.setOnSucceeded(e -> {
      Image image = task.getValue();
      if (image != null && !image.isError()) {
        Platform.runLater(() -> imageView.setImage(task.getValue()));
      }
    });
    new Thread(task).start();

  }

  /**
   * Returns true if the disk cache contains data for the given key.
   *
   * @param key to look for
   * @return true if the disk cache contains key
   */
  public boolean contains(String key) {
    return getDiskCache().containsKey(key);

  }

  /**
   * Clear both the disk cache and the in-memory image cache.
   */
  public void clear() {
    getDiskCache().clear();
    getMemoryCache().clear();

  }

  private Cache<String, byte[]> getDiskCache() {
    return cacheManager.getCache("image-disk-cache", String.class, byte[].class);

  }

  private Cache<String, Image> getMemoryCache() {
    return cacheManager.getCache("image-memory-cache", String.class, Image.class);

  }

  private void persistToDiskCache(String key, Image image, String type) {
    try {
      log.trace("Persisting image {} into disk cache", key);
      BufferedImage buffImage = SwingFXUtils.fromFXImage(image, null);
      ByteArrayOutputStream s = new ByteArrayOutputStream();
      ImageIO.write(buffImage, type, s);
      byte[] res = s.toByteArray();
      s.close(); // especially if you are using a different output stream.
      getDiskCache().put(key, res);
    } catch (IllegalArgumentException | IllegalStateException | IOException e) {
      log.error("Error caching image", e);
    }
  }
}
