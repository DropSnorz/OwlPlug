package com.owlplug.host;

import com.owlplug.host.util.LibraryLoader;

public class NativeHostJNI {

  static {
    LibraryLoader.load("owlplug-host", NativeHostJNI.class, true);
    
  }
  
  public native NativePlugin loadPlugin(String path);
  
  
}
