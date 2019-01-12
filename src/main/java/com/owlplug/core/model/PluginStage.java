package com.owlplug.core.model;

public enum PluginStage {
  BETA("beta"), DEMO("demo"), RELEASE("release");

  private String text;

  PluginStage(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }

  /**
   * Retrieves an enum instance matching a text string. Returns null if the given
   * string doesn't match any defined enum instance.
   * 
   * @param text enum unique text
   * @return
   */
  public static PluginStage fromString(String text) {
    for (PluginStage b : PluginStage.values()) {
      if (b.text.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }

}
