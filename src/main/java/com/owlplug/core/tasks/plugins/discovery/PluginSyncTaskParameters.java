package com.owlplug.core.tasks.plugins.discovery;

import com.owlplug.core.model.platform.RuntimePlatform;

public class PluginSyncTaskParameters {

  private RuntimePlatform platform;
  private String vstDirectory;
  private String vst3Directory;
  private boolean findVst2;
  private boolean findVst3;

  public RuntimePlatform getPlatform() {
    return platform;
  }

  public void setPlatform(RuntimePlatform platform) {
    this.platform = platform;
  }

  public String getVstDirectory() {
    return vstDirectory;
  }

  public void setVstDirectory(String pluginDirectory) {
    this.vstDirectory = pluginDirectory;
  }

  public String getVst3Directory() {
    return vst3Directory;
  }

  public void setVst3Directory(String vst3Directory) {
    this.vst3Directory = vst3Directory;
  }

  public boolean isFindVst2() {
    return findVst2;
  }

  public void setFindVst2(boolean findVst2) {
    this.findVst2 = findVst2;
  }

  public boolean isFindVst3() {
    return findVst3;
  }

  public void setFindVst3(boolean findVst3) {
    this.findVst3 = findVst3;
  }

}
