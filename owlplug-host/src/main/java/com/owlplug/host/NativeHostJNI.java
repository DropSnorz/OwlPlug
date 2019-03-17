package com.owlplug.host;

import com.owlplug.host.util.LibraryLoader;

public class NativeHostJNI {

  private static final String LIB_NAME = "owlplug-host";
  private static final String LIB_VERSION = "0.1.1";
  private static final String LIB_ID = LIB_NAME + "-" + LIB_VERSION;
  
  private static boolean isNativeLibraryLoaded;
  
  static {
    isNativeLibraryLoaded = LibraryLoader.load(LIB_ID, NativeHostJNI.class, false);
    
  }
  
  public static boolean isNativeLibraryLoaded() {
    return isNativeLibraryLoaded;
  }
  
  public native NativePlugin loadPlugin(String path);
  
  
}
