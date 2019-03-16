package com.owlplug.host;

import com.owlplug.host.util.LibraryLoader;

public class NativeHostJNI {

  static {
    LibraryLoader.load("owlplug-host", NativeHostJNI.class, true);
    
  }

  public static void main(String[] args) {
    new NativeHostJNI().sayHello();
  }
  
  public void hello() {
    sayHello();
  }

  // Declare a native method sayHello() that receives no arguments and returns void
  private native void sayHello();
}
