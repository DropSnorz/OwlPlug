package com.owlplug.store.model;

public enum StoreType {

  OWLPLUG_REGISTRY("owlplug-registry"),
  OWLPLUG_STORE("owlplug-store");

  private String value;

  StoreType(String text) {
    this.value = text;
  }

  public String getValue() {
    return value;
  }

}
