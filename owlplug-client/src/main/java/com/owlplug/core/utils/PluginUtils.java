package com.owlplug.core.utils;

public class PluginUtils {

  /**
   * Returns plugin name without platform and architecture qualifiers. Example:
   * input: WobbleIzer_x64 output: WobbleIzer
   * 
   * @param name - the plugin name to filter
   * @return The absolute plugin name
   */
  public static String absoluteName(String name) {

    if (name == null) {
      return null;
    }

    // Remove trailing (XXbit)
    name = name.replaceAll("[(]64bit[)]$", "");
    name = name.replaceAll("[(]32bit[)]$", "");
    // Remove trailing winXX, (?i) for case insensitive regex
    name = name.replaceAll("(?i)win64$", "");
    name = name.replaceAll("(?i)win32$", "");
    // Remove trailing winXXvst, (?i) for case insensitive regex
    name = name.replaceAll("(?i)win64vst$", "");
    name = name.replaceAll("(?i)win32vst$", "");
    // Remove trailing xXX
    name = name.replaceAll("x64$", "");
    name = name.replaceAll("x32$", "");
    // Remove trailing XX
    name = name.replaceAll("64$", "");
    name = name.replaceAll("32$", "");

    // Remove trailing signs
    name = name.replaceAll("(-)*$", "");
    name = name.replaceAll("(_)*$", "");
    name = name.replaceAll("([.])*$", "");

    return name.trim();

  }

}
