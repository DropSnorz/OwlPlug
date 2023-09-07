package com.owlplug.controls;

import java.net.URL;

public final class OwlPlugControlsResources {

  public static URL load(String path) {

    return OwlPlugControlsResources.class.getResource(path);
  }

  private OwlPlugControlsResources() {
  }
}