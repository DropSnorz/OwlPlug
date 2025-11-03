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

package com.owlplug.host.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassPathVersionUtils {

  private static final Logger log = LoggerFactory.getLogger(ClassPathVersionUtils.class);

  public static String getVersion(String resource) throws IOException {

    String filename = resource + ".version";
    InputStream inputStream = ClassPathVersionUtils.class.getClassLoader()
      .getResourceAsStream(filename);
    if (inputStream == null) {
      throw new IOException("Resource " + filename + " not found");
    }

    String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    return text;

  }

  public static String getVersionSafe(String resource) {

    try {
      return getVersion(resource);

    } catch (IOException e) {
      log.error("Version can't be retrieved from resource {}", resource, e);
    }

    return "undefined-version";

  }


}
