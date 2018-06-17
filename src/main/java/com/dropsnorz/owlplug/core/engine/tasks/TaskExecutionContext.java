package com.dropsnorz.owlplug.core.engine.tasks;

import com.dropsnorz.owlplug.core.components.TaskRunner;

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
	
	
	
	

}
