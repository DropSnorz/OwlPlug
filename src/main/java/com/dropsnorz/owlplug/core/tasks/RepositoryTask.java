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
   * 
   * @param strategy   - the startegy to execute
   * @param parameters - the tasks parameters
   * @param repository - the repository used for task execution
   */
  public RepositoryTask(IRepositoryStrategy strategy, RepositoryStrategyParameters parameters,
      PluginRepository repository) {
    super("Repository Update");
    this.strategy = strategy;
    this.parameters = parameters;
    this.repository = repository;

  }

  @Override
  protected TaskResult call() throws Exception {

    this.updateProgress(0, 10);
    try {
      this.updateMessage("Updating repository " + repository.getName());
      this.updateProgress(2, 10);
      strategy.execute(repository, parameters);
      this.updateProgress(10, 10);

    } catch (RepositoryStrategyException e) {

      this.updateMessage(e.getMessage());
      this.updateProgress(5, 10);
      throw new TaskException(e);
    }
    return success();
  }

}
