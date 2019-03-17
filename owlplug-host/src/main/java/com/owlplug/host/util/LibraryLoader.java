package com.owlplug.host.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Custom Library Loader.
 * Loads Native library from regular library path or classpath.
 *
 */
public class LibraryLoader {
  private static String SEPARATOR = System.getProperty("file.separator");
  private static String TMP_PATH = System.getProperty("java.io.tmpdir");
  private static String  LIB_EXTENSION = getPlatformLibraryExtension();
  

  public static boolean load(String libName, Class ref, boolean throwOnFailure) {
    if (load(libName)) {
      return true;
    }
    if (load(TMP_PATH + SEPARATOR + libName + LIB_EXTENSION)) {
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
      File file = new File(TMP_PATH + SEPARATOR + libName + LIB_EXTENSION);
      InputStream is = ref.getClassLoader().getResourceAsStream(libName + LIB_EXTENSION);
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
  
  /**
   * Returns platform default library extension.
   * - Windows host: .dll
   * - Mac host: .dylib
   * Returns an empty string for any other hosts
   * @return host default library extension
   */
  private static String getPlatformLibraryExtension() {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.indexOf("win") >= 0) {
      return ".dll";
    } else if (osName.indexOf("mac") >= 0) {
      return ".dylib";
    }
    return "";
  }
  

}
