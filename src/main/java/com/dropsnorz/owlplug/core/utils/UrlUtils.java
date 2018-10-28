package com.dropsnorz.owlplug.core.utils;

public class UrlUtils {
	
	/**
	 * Replaces spaces with url char %20
	 * @param url - The url
	 * @return url without space chars
	 */
	public static String fixSpaces(String url) {
		
		if (url == null) {
			return null;
		}
		return url.replace(" ", "%20");
	}

}
