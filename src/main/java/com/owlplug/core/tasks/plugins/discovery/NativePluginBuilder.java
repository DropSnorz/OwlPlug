package com.owlplug.core.tasks.plugins.discovery;

import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginFormat;
import java.io.File;

public abstract class NativePluginBuilder {

  protected PluginFormat pluginFormat;

  NativePluginBuilder(PluginFormat pluginFormat) {

    this.pluginFormat = pluginFormat;
  }

  public abstract Plugin build(File file);

}
