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

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Component
public class ImageCache {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CacheManager cacheManager;

	ImageCache(){

	}

	public Image get(String url) {
		return get(url, "png");
	}

	public Image get(String url, String type){

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

		// Load image and add it to the cache once it's done
		Image cachedImage = new Image(url, true);
		cachedImage.progressProperty().addListener((observable, oldValue,  progress) -> {
			if ((Double) progress == 1.0 && ! cachedImage.isError()) {
				try {
					log.debug("Persisting image {} into cache", url);
					BufferedImage bImage = SwingFXUtils.fromFXImage(cachedImage, null);
					ByteArrayOutputStream s = new ByteArrayOutputStream();
					ImageIO.write(bImage, type, s);
					byte[] res  = s.toByteArray();
					s.close(); //especially if you are using a different output stream.
					cache.put(new Element(url, res));
				} catch (IllegalArgumentException | IllegalStateException | CacheException | IOException e) {
					log.error("Error caching image", e);
				}
			}
		});

		return cachedImage;
	}
	

	public void clear() {
		Cache cache = cacheManager.getCache("image-cache");
		cache.removeAll();


	}

}
