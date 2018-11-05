package com.dropsnorz.owlplug.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlUtils {
	
	/**
	 * Replaces spaces with url char %20.
	 * @param url - The url
	 * @return url without space chars
	 */
	public static String fixSpaces(String url) {
		
		if (url == null) {
			return null;
		}
		return url.replace(" ", "%20");
	}
	
	/**
	 * Translates a string into application/x-www-form-urlencodedformat using UTF-8 encoding.
	 * @param query - the query parameter to encode
	 * @return an encoded query parameter
	 */
	public static String encodeQuery(String query) {
		try {
			return URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

}
