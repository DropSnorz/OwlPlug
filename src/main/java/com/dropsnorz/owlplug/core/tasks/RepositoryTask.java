package com.dropsnorz.owlplug.core.tasks;

import com.dropsnorz.owlplug.core.model.PluginRepository;
import com.dropsnorz.owlplug.core.tasks.repositories.IRepositoryStrategy;
import com.dropsnorz.owlplug.core.tasks.repositories.RepositoryStrategyException;
import com.dropsnorz.owlplug.core.tasks.repositories.RepositoryStrategyParameters;

public class RepositoryTask extends AbstractTask {

	private IRepositoryStrategy strategy;
	private RepositoryStrategyParameters parameters;
	private PluginRepository repository;
	
	/**
	 * Creates a new Repository Task.
	 * @param strategy - the startegy to execute
	 * @param parameters - the tasks parameters
	 * @param repository - the repository used for task execution
	 */
	public RepositoryTask(IRepositoryStrategy strategy, RepositoryStrategyParameters parameters, 
			PluginRepository repository) {
		this.strategy = strategy;
		this.parameters = parameters;
		this.repository = repository;
	}
	
	@Override
	protected TaskResult call() throws Exception {
		
		this.updateProgress(0, 1);
		try {
			strategy.execute(repository, parameters);
			this.updateProgress(1, 1);

		} catch (RepositoryStrategyException e) {
			
			this.updateMessage(e.getMessage());
			this.updateProgress(1, 1);
			throw new TaskException(e);
		}
		return success();
	}

}
