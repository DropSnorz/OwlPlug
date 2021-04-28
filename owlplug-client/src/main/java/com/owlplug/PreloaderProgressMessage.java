/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug;

import java.util.Objects;

import javafx.application.Preloader.PreloaderNotification;

public class PreloaderProgressMessage implements PreloaderNotification {
  public static final PreloaderProgressMessage SUCCESSFULLY_DONE = new PreloaderProgressMessage(true, false);
  public static final PreloaderProgressMessage FAILED = new PreloaderProgressMessage(false, true);

  private double progress;
  private String message;
  private String type;
  private boolean done;
  private boolean failed;

  public PreloaderProgressMessage(String type, String message, double progress) {
    this.type = type;
    this.progress = progress;
    this.message = message;
    this.done = false;
    this.failed = false;
  }

  public PreloaderProgressMessage(String type, String message) {
    this.type = type;
    this.message = message;
    this.done = false;
    this.failed = false;
  }

  private PreloaderProgressMessage(boolean done, boolean failed) {
    this.done = done;
    this.failed = failed;
  }

  public double getProgress() {
    return progress;
  }

  public String getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }

  public boolean isDone() {
    return done;
  }

  public boolean isFailed() {
    return failed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PreloaderProgressMessage message1 = (PreloaderProgressMessage) o;
    return Double.compare(message1.progress, progress) == 0 && done == message1.done && failed == message1.failed
        && Objects.equals(message, message1.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(progress, message, done, failed);
  }

  @Override
  public String toString() {
    return "ProgressMessage{" + "progress=" + progress + ", message='" + message + '\'' + ", done=" + done + ", failed="
        + failed + '}';
  }
}
