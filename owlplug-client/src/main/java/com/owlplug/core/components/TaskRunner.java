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
 
package com.owlplug.core.components;

import com.owlplug.core.controllers.TaskBarController;
import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskResult;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * This class stores and executes submitted tasks one by one. Each pending tasks
 * is stored before execution. The runner dispatches execution information to
 * the TaskBarController bean.
 *
 */
@Component
public class TaskRunner {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private TaskBarController taskBarController;

  private SimpleAsyncTaskExecutor exec;
  private LinkedBlockingDeque<AbstractTask> taskQueue;
  private AbstractTask currentTask = null;

  private ArrayList<AbstractTask> taskHistory;

  private TaskRunner() {
    exec = new SimpleAsyncTaskExecutor();
    taskQueue = new LinkedBlockingDeque<AbstractTask>();
    taskHistory = new ArrayList<AbstractTask>();

  }

  /**
   * Submit a task at the end of executor queue.
   * 
   * @param task - the task to submit
   */
  public void submitTask(AbstractTask task) {
    log.debug("Task submitted to queue - {} ", task.getClass().getName());
    taskQueue.addLast(task);
    scheduleNext();

  }

  /**
   * Submit a task in front of the executor queue.
   * 
   * @param task - the task to submit
   */
  public void submitTaskOnQueueHead(AbstractTask task) {
    log.debug("Task submitted to queue - {} ", task.getClass().getName());
    taskQueue.addFirst(task);
    scheduleNext();

  }

  /**
   * Refresh the task runner by submitting the next pending task for execution.
   * 
   */
  @SuppressWarnings({"unchecked"})
  private synchronized void scheduleNext() {

    if (!taskQueue.isEmpty() && currentTask == null) {
      disableError();
      // Get the next pending task
      AbstractTask polledTask = taskQueue.pollFirst();
      setCurrentTask(polledTask);
      addInTaskHistory(currentTask);
      log.debug("Task submitted to executor - {} ", currentTask.getClass().getName());
      ListenableFuture<TaskResult> future = (ListenableFuture<TaskResult>) exec.submitListenable(currentTask);

      future.addCallback(new ListenableFutureCallback<TaskResult>() {
        @Override
        public void onSuccess(TaskResult result) {
          Platform.runLater(() -> {
            if (currentTask.getState().equals(State.FAILED)) {
              if (currentTask.getException() != null) {
                log.error("Error while running task", currentTask.getException());
              }
              triggerOnError();
            }
            removeCurrentTask();
            scheduleNext();
          });
        }

        @Override
        public void onFailure(Throwable ex) {
          log.error("Error while running task", ex);
          Platform.runLater(() -> {
            removeCurrentTask();
            scheduleNext();
          });
        }
      });
    }
  }

  private void setCurrentTask(AbstractTask task) {
    this.currentTask = task;
    // Bind progress indicators
    taskBarController.taskProgressBar.progressProperty().bind(currentTask.progressProperty());
    taskBarController.taskLabel.textProperty().bind(currentTask.messageProperty());
  }

  private void removeCurrentTask() {
    // Unbind progress indicators
    taskBarController.taskProgressBar.progressProperty().unbind();
    taskBarController.taskLabel.textProperty().unbind();

    currentTask = null;

  }

  private void addInTaskHistory(AbstractTask task) {
    if (taskHistory.size() >= 10) {
      taskHistory.remove(0);
    }
    taskHistory.add(task);
  }
  
  public void close() {
    taskQueue.clear();
    AbstractTask pendingTask = currentTask;
    removeCurrentTask();
    if (pendingTask != null) {
      pendingTask.cancel();
    }
    
  }
  

  public List<AbstractTask> getPendingTasks() {
    return new ArrayList<>(taskQueue);
  }

  public List<AbstractTask> getTaskHistory() {
    return new ArrayList<>(taskHistory);
  }

  private void triggerOnError() {
    taskBarController.taskProgressBar.getStyleClass().add("progress-bar-error");

  }

  public void disableError() {
    taskBarController.taskProgressBar.getStyleClass().remove("progress-bar-error");

  }

}
