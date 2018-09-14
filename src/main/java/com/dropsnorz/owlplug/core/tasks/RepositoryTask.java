package com.dropsnorz.owlplug.core.tasks;

public abstract class RepositoryTask extends AbstractTask {

	
	@Override
	protected abstract TaskResult call() throws Exception;

}
