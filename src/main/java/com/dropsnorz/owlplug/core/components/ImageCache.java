package com.dropsnorz.owlplug.core.components;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Component
public class ImageCache {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CacheManager cacheManager;

	ImageCache(){

	}

	public Image get(String url) {
		return get(url, "png");
	}

	public Image get(String url, String type){
		return get(url, type, true);
	}


	public Image get(String url, String type, boolean asyncFetch){

		Cache cache = cacheManager.getCache("image-cache");
		Element cachedElement =  cache.get(url);

		//Retrieve image from cache
		if(cachedElement != null) {
			try {
				log.debug("Retrieving image {} from cache", url);
				byte[] content = (byte[]) cachedElement.getObjectValue();
				ByteArrayInputStream s = new ByteArrayInputStream(content);
				BufferedImage bImage = ImageIO.read(s);
				return SwingFXUtils.toFXImage(bImage, null);
			} catch (IOException e) {
				log.error("Error retrieving image from cache", e);
			}
		}

		// Load image
		Image cachedImage = new Image(url, asyncFetch);
		// In case of async fetch, persist to the cache on complete
		cachedImage.progressProperty().addListener((observable, oldValue,  progress) -> {
			if ((Double) progress == 1.0 && ! cachedImage.isError()) {
				persistImageIntoCache(cache, url, cachedImage, type);
			}
		});
		// In case of sync fetch, persist image in cache immediately
		if(!asyncFetch && !cachedImage.isError()) {
			persistImageIntoCache(cache, url, cachedImage, type);

		}

		return cachedImage;
	}


	public void loadAsync(String url, ImageView imageView) {

		Task<Image> task = new Task<Image>() {
			public Image call() {
				return ImageCache.this.get(url ,"png", false);
			}
		};

		task.setOnSucceeded((e) -> {
			Image image = task.getValue();
			if (!image.isError()) {
				Platform.runLater(() -> {
					imageView.setImage(task.getValue());
				});
			}
		});
		new Thread(task).start();

	}
	
	public boolean contains(String key) {
		Cache cache = cacheManager.getCache("image-cache");
		return cache.isKeyInCache(key);

	}
	
	public void clear() {
		Cache cache = cacheManager.getCache("image-cache");
		cache.removeAll();


	}
	
	private void persistImageIntoCache(Cache cache, String key, Image image, String type) {
		try {
			log.debug("Persisting image {} into cache", key);
			BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			ImageIO.write(bImage, type, s);
			byte[] res  = s.toByteArray();
			s.close(); //especially if you are using a different output stream.
			cache.put(new Element(key, res));
		} catch (IllegalArgumentException | IllegalStateException | CacheException | IOException e) {
			log.error("Error caching image", e);
		}
	}

}
