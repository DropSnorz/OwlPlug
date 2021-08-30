package com.owlplug.core.model;

public enum PluginState {

  INSTALLED("Installed", "Plugin has been detected by OwlPlug"),
  DISABLED("Disabled", "Plugin is disabled"),
  UNSTABLE("Unstable", "Last plugin scan crashed OwlPlug"),
  ACTIVE("Active", "Plugin is working properly");

  private String text;
  private String description;

  PluginState(String text, String description) {
    this.text = text;
    this.description = description;
  }

  public String getText() {
    return text;
  }

  public String getDescription() {
    return description;
  }
}
