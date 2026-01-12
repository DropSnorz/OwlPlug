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

import com.owlplug.host.model.OS;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Library Loader.
 * Loads Native library from regular library path or classpath.
 *
 */
public final class LibraryLoader {

  private static final Logger log = LoggerFactory.getLogger(LibraryLoader.class);

  private static final Path TMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"));
  private static final String LIB_EXTENSION = platformExtension();

  private LibraryLoader() {
  }

  public static boolean load(String libName, Class<?> referenceClass) {
    if (tryLoadByName(libName)) return true;

    Path tempLib = TMP_DIR.resolve(libName + LIB_EXTENSION);
    if (tryLoadByPath(tempLib)) return true;

    return extractAndLoad(referenceClass, libName, tempLib);
  }

  private static boolean tryLoadByName(String libName) {
    try {
      System.loadLibrary(libName);
      log.info("Loaded library by name: {}", libName);
      return true;
    } catch (UnsatisfiedLinkError e) {
      log.debug("Failed to load library by name: {}", libName, e);
      return false;
    }
  }

  private static boolean tryLoadByPath(Path path) {
    if (!Files.exists(path)) {
      return false;
    }

    try {
      System.load(path.toAbsolutePath().toString());
      log.info("Loaded library from path: {}", path);
      return true;
    } catch (UnsatisfiedLinkError e) {
      log.debug("Failed to load library from path: {}", path, e);
      return false;
    }
  }

  private static boolean extractAndLoad(Class<?> ref, String libName, Path target) {
    String resourceName = libName + LIB_EXTENSION;

    try (InputStream in = ref.getClassLoader().getResourceAsStream(resourceName)) {
      if (in == null) {
        log.debug("Library {} not found on classpath", resourceName);
        return false;
      }

      Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
      log.debug("Extracted library to {}", target);

      return tryLoadByPath(target);

    } catch (IOException e) {
      log.error("Failed to extract native library {}", resourceName, e);
      return false;
    }
  }

  private static String platformExtension() {
    if (OS.WINDOWS.isCurrentOs()) return ".dll";
    if (OS.MAC.isCurrentOs()) return ".dylib";
    if (OS.LINUX.isCurrentOs()) return ".so";

    throw new UnsupportedOperationException(
        "Unsupported OS for native libraries: " + OS.current());
  }
}

