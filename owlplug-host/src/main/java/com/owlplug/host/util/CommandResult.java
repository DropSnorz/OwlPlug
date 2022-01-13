package com.owlplug.host.util;

public class CommandResult {

  private int exitValue;
  private String output;

  public CommandResult(int exitValue, String output) {
    this.exitValue = exitValue;
    this.output = output;
  }

  public int getExitValue() {
    return exitValue;
  }

  public void setExitValue(int exitValue) {
    this.exitValue = exitValue;
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
  }
}
