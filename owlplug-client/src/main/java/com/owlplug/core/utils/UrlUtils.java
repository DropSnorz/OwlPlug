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
 
package com.owlplug.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlUtils {

  /**
   * Replaces spaces with url char %20.
   * 
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
   * Translates a string into application/x-www-form-urlencodedformat using UTF-8
   * encoding.
   * 
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
