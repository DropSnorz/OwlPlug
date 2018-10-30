package com.dropsnorz.owlplug.core.tasks;

import com.dropsnorz.owlplug.core.components.TaskRunner;

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
	
	public void run() {
		taskRunner.submitTask(task);
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
