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

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtils {

  public static String truncate(String str, int size, String suffix) {
    if (str == null || size < 0) {
      return "";
    }
    if (str.length() > size) {
      return str.substring(0, Math.max(0, size - suffix.length())) + suffix;
    }
    return str;
  }

  public static String ellipsis(String input, int maxLength, int clearEndLength) {
    if (input == null || input.length() <= maxLength
            || clearEndLength >= maxLength) {
      return input;
    } else {
      String truncatedString = input.substring(0, maxLength - clearEndLength);
      return truncatedString + "..." + input.substring(input.length() - clearEndLength);
    }
  }

  public static String getStackTraceAsString(Throwable throwable) {
    if (throwable == null) {
      return "null throwable";
    }
    StringWriter sw = new StringWriter();
    throwable.printStackTrace(new PrintWriter(sw));
    return sw.toString();
  }
}
