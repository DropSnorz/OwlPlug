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
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformUtils {

  private static final Logger log = LoggerFactory.getLogger(PlatformUtils.class);

  private PlatformUtils() {
  }

  public static void openFromDesktop(String path) {
    if (path != null) {
      openFromDesktop(new File(path));
    } else {
      throw new IllegalArgumentException("path can't be null");
    }
  }

  public static void openFromDesktop(File file) {
    Async.run(() -> {
      if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Action.OPEN)) {
        log.warn("Desktop API not supported, cannot open file: {}", file.getAbsolutePath());
        return;
      }
      try {
        Desktop.getDesktop().open(file);
      } catch (IOException e) {
        log.error("Application for the given file fails to be launched", e);
      }
    });
  }

  public static void openDefaultBrowser(String url) {
    Async.run(() -> {
      try {
        log.debug("Opening address {} in default browser", url);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
          Desktop.getDesktop().browse(new URI(url));
        } else {
          log.warn("Desktop API not supported, falling back to xdg-open");
          Process process = new ProcessBuilder("xdg-open", url).start();
          if (!process.waitFor(5, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            log.warn("xdg-open timed out for URL: {}", url);
          } else if (process.exitValue() != 0) {
            log.error("xdg-open failed for URL: {} (exit code {})", url, process.exitValue());
          }
        }
      } catch (IOException e) {
        log.error("Can't open default browser for URL: {}", url, e);
      } catch (URISyntaxException e) {
        log.error("Invalid URI: {}", url);
      } catch (InterruptedException e) {
        log.error("Thread interrupted while waiting for xdg-open to finish", e);
      }
    });
  }

}
