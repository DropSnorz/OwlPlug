package com.dropsnorz.owlplug.core.engine.tasks;

import javafx.concurrent.Task;

public abstract class RepositoryTask extends AbstractTask {

	
	public RepositoryTask() {
		
	}
	@Override
	protected abstract TaskResult call() throws Exception;

}
