package com.owlplug.host.loaders;

import com.owlplug.host.NativePlugin;

public class DummyPluginLoader implements NativePluginLoader {

  private static DummyPluginLoader INSTANCE;

  public static DummyPluginLoader getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DummyPluginLoader();
    }
    return INSTANCE;
  }

  private DummyPluginLoader() {
    // Private constructor for singleton
  }

  @Override
  public void init() {

  }

  @Override
  public void open() {

  }

  @Override
  public NativePlugin loadPlugin(String path) {
    return null;
  }

  @Override
  public void close() {

  }

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public String getName() {
    return "No loader";
  }

  @Override
  public String getId() {
    return "dummy-loader";
  }

  @Override
  public String toString() {
    return this.getName();
  }
}
