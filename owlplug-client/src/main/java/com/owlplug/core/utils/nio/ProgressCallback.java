package com.owlplug.core.utils.nio;

@FunctionalInterface
public interface ProgressCallback {
  public void onProgress(double progress);

}
