package com.owlplug.host.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Custom Library Loader.
 * Loads Native library from regular library path or classpath.
 * TODO: Export library from classpath each time to override new versions
 * TODO: Remove platform specific file extensions
 * @author Arthur
 *
 */
public class LibraryLoader {
  private static String SEPARATOR;
  private static String TMP_PATH;
  
  static {
    SEPARATOR = System.getProperty("file.separator");
    TMP_PATH = System.getProperty("java.io.tmpdir");
    TMP_PATH = new File(TMP_PATH).getAbsolutePath();
  }

  public static boolean load(String libName, Class ref, boolean throwOnFailure) {
    if (load(libName)) {
      return true;
    }
    if (load(TMP_PATH + SEPARATOR + libName + ".dll")) {
      return true;
    }
    if (extract(ref, libName)) {
      return true;
    }

    if (throwOnFailure) {
      throw new UnsatisfiedLinkError("no " + libName 
          + " in java.library.path or on the classpath");
    } else {
      return false;
    }
  }
  
  /**
   * Loads library from path or by library name.
   * @param libName library path or name
   * @return
   */
  public static boolean load(String libName) {
    try {
      if (libName.indexOf(SEPARATOR) != -1) {
        System.load(libName);
      } else {
        System.loadLibrary(libName);
      }
      return true;
    } catch (UnsatisfiedLinkError e) {
      e.printStackTrace();
    }

    return false;
  }

  /**
   * Extracts and load the library from a temp directory.
   * @param ref JNI Class
   * @param libName library name
   * @return
   */
  public static boolean extract(Class ref, String libName) {
    try {
      File file = new File(TMP_PATH + SEPARATOR + libName + ".dll");
      InputStream is = ref.getClassLoader().getResourceAsStream(libName + ".dll");
      if (is == null) {
        return false;

      }
      if (is != null) {
        int read;
        byte[] buffer = new byte[4096];
        FileOutputStream os = new FileOutputStream(file);
        while ((read = is.read(buffer)) != -1) {
          os.write(buffer, 0, read);
        }
        os.close();
        is.close();
        if (load(file.getAbsolutePath())) {
          return true;
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }

    return false;
  }

}
