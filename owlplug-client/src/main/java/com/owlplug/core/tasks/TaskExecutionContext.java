/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import com.owlplug.core.components.TaskRunner;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class TaskExecutionContext {

  private TaskRunner taskRunner;
  private AbstractTask task;

  public TaskExecutionContext(AbstractTask task, TaskRunner taskRunner) {
    this.taskRunner = taskRunner;
    this.task = task;
  }

  public AbstractTask getTask() {
    return task;
  }

  public void schedule() {
    taskRunner.submitTask(task);
  }

  public void scheduleNow() {
    taskRunner.submitTaskOnQueueHead(task);
  }

  public TaskExecutionContext setOnSucceeded(EventHandler<WorkerStateEvent> value) {
    task.setOnSucceeded(value);
    return this;
  }

  public TaskExecutionContext setOnCancelled(EventHandler<WorkerStateEvent> value) {
    task.setOnCancelled(value);
    return this;
  }

  public TaskExecutionContext setOnFailed(EventHandler<WorkerStateEvent> value) {
    task.setOnFailed(value);
    return this;
  }

  public TaskExecutionContext setOnRunning(EventHandler<WorkerStateEvent> value) {
    task.setOnRunning(value);
    return this;
  }

  public TaskExecutionContext setOnScheduled(EventHandler<WorkerStateEvent> value) {
    task.setOnScheduled(value);
    return this;
  }

}
