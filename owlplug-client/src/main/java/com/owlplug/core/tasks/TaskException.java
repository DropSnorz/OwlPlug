package com.owlplug.core.tasks;

public class TaskException extends Exception {

  public TaskException(Exception e) {
    super(e);
  }

  public TaskException(String message) {
    super(message);
  }

  public TaskException(String message, Exception e) {
    super(message, e);
  }
}
