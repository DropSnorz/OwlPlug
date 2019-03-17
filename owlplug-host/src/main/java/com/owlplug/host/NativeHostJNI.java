package com.owlplug.host;

import com.owlplug.host.util.LibraryLoader;

public class NativeHostJNI {

  
  private static boolean isNativeLibraryLoaded;
  
  static {
    isNativeLibraryLoaded = LibraryLoader.load("owlplug-host", NativeHostJNI.class, false);
    
  }
  
  public static boolean isNativeLibraryLoaded() {
    return isNativeLibraryLoaded;
  }
  
  public native NativePlugin loadPlugin(String path);
  
  
}
