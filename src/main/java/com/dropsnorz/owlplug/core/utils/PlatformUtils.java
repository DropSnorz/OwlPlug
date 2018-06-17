package com.dropsnorz.owlplug.core.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformUtils {
	
	private PlatformUtils() {}
	
	private static final Logger log = LoggerFactory.getLogger(PlatformUtils.class);

	public static void openDirectoryExplorer(String path){
		try {
			Desktop.getDesktop().open(new File(path));
		} catch (IOException e) {
			log.error("Application for the given file fails to be launched",e);
		}
	}
	
	public static void openDirectoryExplorer(File file){
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			log.error("Application for the given file fails to be launched",e);
		}
	}

}
