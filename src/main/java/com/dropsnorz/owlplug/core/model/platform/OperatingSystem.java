package com.dropsnorz.owlplug.core.model.platform;

public enum OperatingSystem {
  WIN("win"), MAC("osx"), UNIX("unix"), UNDEFINED("undefined");

  private String code;

  OperatingSystem(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

}
