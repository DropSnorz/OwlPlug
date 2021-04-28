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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformUtils {

  private static final Logger log = LoggerFactory.getLogger(PlatformUtils.class);

  private PlatformUtils() {
  }

  public static void openDirectoryExplorer(String path) {
    if (path != null) {
      openDirectoryExplorer(new File(path));
    } else {
      throw new IllegalArgumentException("path can't be null");
    }
  }

  public static void openDirectoryExplorer(File file) {
    try {
      Desktop.getDesktop().open(file);
    } catch (IOException e) {
      log.error("Application for the given file fails to be launched", e);
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
