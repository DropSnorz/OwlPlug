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
 
package com.owlplug.core.tasks;

import java.util.ArrayList;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTask extends Task<TaskResult> {
  
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private String name = "OwlPlug task";

  private double maxProgress = 1;
  private double committedProgress = 0;

  private ArrayList<String> warnings = new ArrayList<>();

  public AbstractTask() {
  }

  public AbstractTask(String name) {
    this.name = name;
  }

  protected void commitProgress(double progress) {
    committedProgress = committedProgress + progress;
    this.updateProgress(committedProgress, getMaxProgress());

  }

  protected double getCommittedProgress() {
    return committedProgress;
  }

  protected void setCommittedProgress(double committedProgress) {
    this.committedProgress = committedProgress;

  }

  protected double getMaxProgress() {
    return maxProgress;
  }

  protected void setMaxProgress(double maxProgress) {
    this.maxProgress = maxProgress;
  }

  protected void computeTotalProgress(double progress) {
    updateProgress(committedProgress + progress, maxProgress);
  }

  protected TaskResult success() {
    return new TaskResult();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  protected ArrayList<String> getWarnings() {
    return warnings;
  }
  
  @Override
  protected void updateMessage(String message) {
    log.trace("Task status update [" + message + "]");
    super.updateMessage(message);
  }

  @Override
  public String toString() {
    String prefix = "W";
    if (this.isRunning()) {
      prefix = "R";
    }
    if (this.isDone()) {
      prefix = "D";
    }
    if (this.getState().equals(State.FAILED)) {
      prefix = "F";
    }
    return prefix + " - " + this.getName();

  }
}
