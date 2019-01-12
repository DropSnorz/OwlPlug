package com.dropsnorz.owlplug.core.tasks.plugins.discovery;

import com.dropsnorz.owlplug.core.model.PluginFormat;
import com.dropsnorz.owlplug.core.model.platform.OperatingSystem;

public class NativePluginBuilderFactory {

  public static NativePluginBuilder createPluginBuilder(OperatingSystem osType, PluginFormat pluginFormat) {

    switch (osType) {
    case WIN:
      return new WindowsPluginBuilder(pluginFormat);
    case MAC:
      return new OSXPluginBuilder(pluginFormat);
    default:
      break;
    }

    return null;

  }

}
