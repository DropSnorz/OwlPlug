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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClassPathFileExtractor {

  private static final Logger log = LoggerFactory.getLogger(LibraryLoader.class);

  public static void extract(Class classRef, String resourceName, File outputFile) throws IOException {

    log.debug("Extracting resource to " + outputFile.getAbsolutePath());
    InputStream is = classRef.getClassLoader().getResourceAsStream(resourceName);
    if (is == null) {
      log.error("Resource " + resourceName + " not in classpath");
    }
    if (is != null) {
      int read;
      byte[] buffer = new byte[4096];
      FileOutputStream os = new FileOutputStream(outputFile);
      while ((read = is.read(buffer)) != -1) {
        os.write(buffer, 0, read);
      }
      os.close();
      is.close();
    }

  }

}
