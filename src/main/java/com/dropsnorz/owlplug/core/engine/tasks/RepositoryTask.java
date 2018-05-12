package com.dropsnorz.owlplug.core.engine.tasks;

import javafx.concurrent.Task;

public abstract class RepositoryTask extends Task<Void> {

	
	public RepositoryTask() {
		
	}
	@Override
	protected abstract Void call() throws Exception;

}
