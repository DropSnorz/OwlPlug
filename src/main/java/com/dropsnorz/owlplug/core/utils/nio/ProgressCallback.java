package com.dropsnorz.owlplug.core.utils.nio;

@FunctionalInterface
public interface ProgressCallback {
  public void onProgress(double progress);

}
