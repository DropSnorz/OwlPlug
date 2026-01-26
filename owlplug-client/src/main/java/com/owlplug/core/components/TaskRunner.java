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
import com.owlplug.core.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

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

  private final SimpleAsyncTaskExecutor executor;
  private final LinkedBlockingDeque<AbstractTask> taskQueue;
  private final ArrayList<AbstractTask> taskHistory;

  private AbstractTask currentTask = null;

  private TaskRunner() {
    executor = new SimpleAsyncTaskExecutor();
    taskQueue = new LinkedBlockingDeque<>();
    taskHistory = new ArrayList<>();

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
    log.debug("Task submitted to queue head - {} ", task.getClass().getName());
    taskQueue.addFirst(task);
    scheduleNext();

  }

  /**
   * Refresh the task runner by submitting the next pending task for execution.
   * 
   */
  private synchronized void scheduleNext() {

    if (!taskQueue.isEmpty() && currentTask == null) {
      taskBarController.resetErrorLog();
      // Get the next pending task
      AbstractTask polledTask = taskQueue.pollFirst();
      setCurrentTask(polledTask);
      addInTaskHistory(currentTask);
      log.debug("Task submitted to executor - {} ", currentTask.getClass().getName());

      CompletableFuture<TaskResult> future = submitCompletable(currentTask, executor);

      future.thenAccept(result -> {
        Platform.runLater(() -> {
          removeCurrentTask();
          scheduleNext();
        });
      }).exceptionally(ex -> {
        log.error("Error while running task", ex);
        Platform.runLater(() -> {
          if (ex != null) {
            taskBarController.setErrorLog(
                currentTask,
                ex.getMessage(),
                StringUtils.getStackTraceAsString(ex),
                NestedExceptionUtils.getMostSpecificCause(ex).getMessage()
            );
          } else {
            taskBarController.setErrorLog(
                currentTask,
                "Task failed",
                "No error information available.",
                "No summary available"
            );
          }
          removeCurrentTask();
          scheduleNext();
        });
        return null;
      });

    }
  }

  /**
   * Submit a task to an executor and return a CompletableFuture that will be
   * completed when the task finishes.
   * This completable Future is a wrapper to manipulate task result and state
   * as a replacement of deprecated Spring ListenableFuture.
   * @param task task to execute
   * @param exec executor to use
   * @return  completableFuture of task result
   */
  private CompletableFuture<TaskResult> submitCompletable(AbstractTask task, Executor exec) {
    CompletableFuture<TaskResult> cf = new CompletableFuture<>();
    task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
        e -> cf.complete(task.getValue()));
    task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED,
        e -> cf.completeExceptionally(task.getException()));
    task.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED,
        e -> cf.cancel(true));

    exec.execute(task);
    return cf;
  }

  private void setCurrentTask(AbstractTask task) {
    this.currentTask = task;
    // Bind progress indicators
    taskBarController.progressProperty().bind(currentTask.progressProperty());
    taskBarController.taskNameProperty().bind(currentTask.messageProperty());
  }

  private void removeCurrentTask() {
    // Unbind progress indicators
    taskBarController.progressProperty().unbind();
    taskBarController.taskNameProperty().unbind();

    currentTask = null;

  }

  private void addInTaskHistory(AbstractTask task) {
    if (taskHistory.size() >= 10) {
      taskHistory.removeFirst();
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


}
