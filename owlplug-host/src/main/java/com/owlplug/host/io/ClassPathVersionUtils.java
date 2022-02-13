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
