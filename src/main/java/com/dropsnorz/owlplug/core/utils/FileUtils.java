package com.dropsnorz.owlplug.core.utils;

public class FileUtils {

	public static String convertPath(String path) {
		return path.replace("\\", "/");
	}

	public static boolean isFilenameValid(String file) {
		
		return file != null && !file.equals("") && file.matches("[-_.A-Za-z0-9]*");
	}

}
