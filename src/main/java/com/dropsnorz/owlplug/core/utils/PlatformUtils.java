package com.dropsnorz.owlplug.core.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformUtils {

	private static final Logger log = LoggerFactory.getLogger(PlatformUtils.class);

	private PlatformUtils() {}

	public static void openDirectoryExplorer(String path) {
		openDirectoryExplorer(new File(path));
	}

	public static void openDirectoryExplorer(File file) {
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			log.error("Application for the given file fails to be launched",e);
		}
	}

	public static void openDefaultBrowser(String url) {

		try {
			if (Desktop.isDesktopSupported()) {
				log.debug("Opening address " + url + " in default browser");
				Desktop.getDesktop().browse(new URI(url));
			}
		} catch (IOException e) {
			log.error("Can't open default browser");
		} catch (URISyntaxException e) {
			log.error("Error in URI:" + url);
		}
	}

}
