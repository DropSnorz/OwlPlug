package com.dropsnorz.owlplug.core.engine.tasks;

import javafx.concurrent.Task;

public abstract class AbstractTask extends Task<TaskResult> {
	
	protected TaskResult success() {
		return new TaskResult();
	}

}
