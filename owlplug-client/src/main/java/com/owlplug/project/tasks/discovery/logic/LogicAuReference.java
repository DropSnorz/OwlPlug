package com.owlplug.project.tasks.discovery.logic;

public record LogicAuReference(String typeCode, String subtype, String manufacturer, String displayName, int offset) {

  public String fingerprint() {
    return typeCode + "/" + subtype + "/" + manufacturer;
  }
}