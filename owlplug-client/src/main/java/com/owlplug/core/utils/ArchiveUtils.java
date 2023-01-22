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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveUtils {

  private static final Logger log = LoggerFactory.getLogger(ArchiveUtils.class);

  public static void extract(String source, String dest) {
    File sourceFile = new File(source);
    File destDirectory = new File(dest);

    extract(sourceFile, destDirectory);
  }

  public static void extract(File source, File dest) {
    try {
      uncompress(source, dest);
    } catch (Exception e) {
      log.error("Error extracting archive {} at {}", source.getAbsolutePath(),
          dest.getAbsolutePath(), e);
      throw new RuntimeException(e);
    }

  }

  private static boolean isCompressed(File file) throws IOException {
    log.debug("Verify file compression: {}", file.getAbsolutePath());
    try (InputStream inputStream = new FileInputStream(file);
          InputStream bufferedIn = new BufferedInputStream(inputStream)) {
      String comp = CompressorStreamFactory.detect(bufferedIn);
      log.debug("Compression signature found: {}", comp);
      return true;
    } catch (CompressorException e) {
      log.debug("Compression signature not found");
      return false;
    }

  }

  private static void uncompress(File sourceFile, File destinationDirectory) throws IOException {

    if (isCompressed(sourceFile)) {
      try (InputStream fi = new FileInputStream(sourceFile);
           InputStream bi = new BufferedInputStream(fi);
           CompressorInputStream gzi = new CompressorStreamFactory().createCompressorInputStream(bi);
           InputStream bgzi = new BufferedInputStream(gzi);
           ArchiveInputStream o = new ArchiveStreamFactory().createArchiveInputStream(bgzi)) {

        uncompress(o, destinationDirectory);
      } catch (CompressorException e) {
        throw new IOException("Error while uncompressing the archive stream: " + sourceFile.getAbsolutePath(), e);
      } catch (ArchiveException e) {
        throw new IOException("Error while extracting the archive stream: " + sourceFile.getAbsolutePath(), e);
      }

    } else {
      try (InputStream fi = new FileInputStream(sourceFile);
           InputStream bi = new BufferedInputStream(fi);
           ArchiveInputStream o = new ArchiveStreamFactory().createArchiveInputStream(bi)) {

        uncompress(o, destinationDirectory);
      } catch (ArchiveException e) {
        throw new IOException("Error while extracting the archive stream: " + sourceFile.getAbsolutePath(), e);
      }
    }
  }

  private static void uncompress(ArchiveInputStream o, File destinationDirectory) throws IOException {

    ArchiveEntry entry = null;
    while ((entry = o.getNextEntry()) != null) {
      if (!o.canReadEntryData(entry)) {
        log.debug("Stream entry cannot be read: {}", entry.getName());
        continue;
      }

      File f = new File(destinationDirectory, entry.getName());
      if (entry.isDirectory()) {
        if (!f.isDirectory() && !f.mkdirs()) {
          throw new IOException("failed to create directory " + f);
        }
      } else {
        File parent = f.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
          throw new IOException("failed to create directory " + parent);
        }
        try (OutputStream output = Files.newOutputStream(f.toPath())) {
          IOUtils.copy(o, output);
        }
      }
    }
  }

}
