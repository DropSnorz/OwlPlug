package com.owlplug.host.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
