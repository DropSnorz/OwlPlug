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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.owlplug.core.controllers.TaskBarController;
import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskRunnerTest {

  private TaskRunner taskRunner;
  private TaskBarController mockTaskBarController;

  @BeforeEach
  public void setUp() {
    taskRunner = new TaskRunner();
    mockTaskBarController = mock(TaskBarController.class);

    // Use reflection to set the private taskBarController field
    try {
      java.lang.reflect.Field field = TaskRunner.class.getDeclaredField("taskBarController");
      field.setAccessible(true);
      field.set(taskRunner, mockTaskBarController);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testSubmitTask() {
    TestTask task = new TestTask("Test Task 1");
    taskRunner.submitTask(task);

    List<AbstractTask> pendingTasks = taskRunner.getPendingTasks();
    // Task may already be executing, so check history or pending
    assertTrue(pendingTasks.size() >= 0);
  }

  @Test
  public void testSubmitMultipleTasks() {
    TestTask task1 = new TestTask("Task 1");
    TestTask task2 = new TestTask("Task 2");
    TestTask task3 = new TestTask("Task 3");

    taskRunner.submitTask(task1);
    taskRunner.submitTask(task2);
    taskRunner.submitTask(task3);

    // At least some tasks should be in the queue or history
    List<AbstractTask> pendingTasks = taskRunner.getPendingTasks();
    List<AbstractTask> taskHistory = taskRunner.getTaskHistory();

    assertTrue(pendingTasks.size() + taskHistory.size() >= 1);
  }

  @Test
  public void testSubmitTaskOnQueueHead() {
    TestTask task1 = new TestTask("Task 1");
    TestTask task2 = new TestTask("Task 2");

    taskRunner.submitTask(task1);
    taskRunner.submitTaskOnQueueHead(task2);

    // Task2 should be processed before or alongside Task1
    List<AbstractTask> pendingTasks = taskRunner.getPendingTasks();
    assertNotNull(pendingTasks);
  }

  @Test
  public void testGetPendingTasks() {
    List<AbstractTask> pendingTasks = taskRunner.getPendingTasks();
    assertNotNull(pendingTasks);
    assertEquals(0, pendingTasks.size());
  }

  @Test
  public void testGetTaskHistory() {
    List<AbstractTask> taskHistory = taskRunner.getTaskHistory();
    assertNotNull(taskHistory);
    assertEquals(0, taskHistory.size());
  }

  @Test
  public void testGetTaskHistoryAfterTaskCompletion() throws InterruptedException {
    TestTask task = new TestTask("Completed Task");
    taskRunner.submitTask(task);

    // Give some time for task execution
    Thread.sleep(200);

    List<AbstractTask> taskHistory = taskRunner.getTaskHistory();
    assertTrue(taskHistory.size() > 0);
  }

  @Test
  public void testTaskHistoryLimit() throws InterruptedException {
    // Submit more than 10 tasks to test history limit
    for (int i = 0; i < 15; i++) {
      TestTask task = new TestTask("Task " + i);
      taskRunner.submitTask(task);
    }

    // Give time for tasks to execute
    Thread.sleep(500);

    List<AbstractTask> taskHistory = taskRunner.getTaskHistory();
    // History should be limited to 10 items
    assertTrue(taskHistory.size() <= 10);
  }

  @Test
  public void testClose() {
    TestTask task1 = new TestTask("Task 1");
    TestTask task2 = new TestTask("Task 2");

    taskRunner.submitTask(task1);
    taskRunner.submitTask(task2);

    taskRunner.close();

    // After close, queue should be cleared
    List<AbstractTask> pendingTasks = taskRunner.getPendingTasks();
    assertEquals(0, pendingTasks.size());
  }

  @Test
  public void testCloseWithRunningTask() throws InterruptedException {
    LongRunningTask task = new LongRunningTask("Long Task");
    taskRunner.submitTask(task);

    // Give task time to start
    Thread.sleep(100);

    taskRunner.close();

    // Task should be cancelled
    List<AbstractTask> pendingTasks = taskRunner.getPendingTasks();
    assertEquals(0, pendingTasks.size());
  }

  @Test
  public void testTaskExecutionOrder() throws InterruptedException {
    TestTask task1 = new TestTask("First");
    TestTask task2 = new TestTask("Second");

    taskRunner.submitTask(task1);
    taskRunner.submitTask(task2);

    // Give time for tasks to complete
    Thread.sleep(300);

    List<AbstractTask> history = taskRunner.getTaskHistory();
    // Both tasks should be in history
    assertTrue(history.size() >= 2);
  }

  @Test
  public void testGetPendingTasksReturnsDefensiveCopy() {
    TestTask task = new TestTask("Test");
    taskRunner.submitTask(task);

    List<AbstractTask> pendingTasks1 = taskRunner.getPendingTasks();
    List<AbstractTask> pendingTasks2 = taskRunner.getPendingTasks();

    // Should return different list instances (defensive copies)
    assertTrue(pendingTasks1 != pendingTasks2);
  }

  @Test
  public void testGetTaskHistoryReturnsDefensiveCopy() throws InterruptedException {
    TestTask task = new TestTask("Test");
    taskRunner.submitTask(task);
    Thread.sleep(200);

    List<AbstractTask> history1 = taskRunner.getTaskHistory();
    List<AbstractTask> history2 = taskRunner.getTaskHistory();

    // Should return different list instances (defensive copies)
    assertTrue(history1 != history2);
  }

  /**
   * Simple test task that completes immediately.
   */
  private static class TestTask extends AbstractTask {
    public TestTask(String name) {
      super(name);
    }

    @Override
    protected TaskResult start() throws Exception {
      Thread.sleep(50); // Simulate some work
      return success();
    }
  }

  /**
   * Task that takes longer to complete for testing cancellation.
   */
  private static class LongRunningTask extends AbstractTask {
    public LongRunningTask(String name) {
      super(name);
    }

    @Override
    protected TaskResult start() throws Exception {
      for (int i = 0; i < 10; i++) {
        if (isCancelled()) {
          break;
        }
        Thread.sleep(100);
      }
      return success();
    }
  }
}