package com.owlplug.host.loaders.jni;

import com.owlplug.host.NativePlugin;
import com.owlplug.host.loaders.NativePluginLoader;

public class JNINativePluginLoader implements NativePluginLoader {

  private static JNINativePluginLoader INSTANCE;
  JNIPluginMapper nativePluginMapper;

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
    nativePluginMapper = JNIPluginMapper.getInstance();
    nativePluginMapper.init();
  }

  @Override
  public void open() {

  }

  @Override
  public NativePlugin loadPlugin(String path) {
    return nativePluginMapper.mapPlugin(path);
  }

  @Override
  public void close() {

  }

  @Override
  public boolean isAvailable() {
    return nativePluginMapper.isNativeLibraryLoaded();
  }

  @Override
  public String getName() {
    return "OwlPlug JNI (legacy)";
  }
}
