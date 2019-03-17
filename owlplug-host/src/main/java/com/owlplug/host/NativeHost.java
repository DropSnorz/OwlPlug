package com.owlplug.host;

public class NativeHost {

  private NativeHostJNI jni;
  
  public NativeHost() {
    jni = new NativeHostJNI();
  }
  
  public boolean isAvailable() {
    return NativeHostJNI.isNativeLibraryLoaded();
    
  }
  
  public NativePlugin loadPlugin(String path) {
    return jni.loadPlugin(path);
  }
  

}
