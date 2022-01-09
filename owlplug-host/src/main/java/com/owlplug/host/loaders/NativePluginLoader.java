package com.owlplug.host.loaders;

import com.owlplug.host.NativePlugin;

public interface NativePluginLoader {

  public void init();
  public void open();
  public NativePlugin loadPlugin(String path);
  public void close();

  public boolean isAvailable();
  public String getName();

}
