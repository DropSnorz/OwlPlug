package com.owlplug.host.loaders.jni;

import com.owlplug.host.NativePlugin;
import com.owlplug.host.loaders.NativePluginLoader;

public class JNINativePluginLoader implements NativePluginLoader {

  private static JNINativePluginLoader INSTANCE;
  JNIPluginMapper nativeHost;

  private JNINativePluginLoader(){

  }

  public static JNINativePluginLoader getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new JNINativePluginLoader();
    }
    return INSTANCE;
  }

  @Override
  public void init() {
    nativeHost = JNIPluginMapper.getInstance();
    nativeHost.init();
  }

  @Override
  public void open() {

  }

  @Override
  public NativePlugin loadPlugin(String path) {
    return nativeHost.mapPlugin(path);
  }

  @Override
  public void close() {

  }

  @Override
  public boolean isAvailable() {
    return nativeHost.isNativeLibraryLoaded();
  }

  @Override
  public String getName() {
    return "OwlPlug JNI (legacy)";
  }
}
