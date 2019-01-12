package com.owlplug.core.tasks.repositories;

import com.owlplug.core.model.PluginRepository;

public interface IRepositoryStrategy {

  public void execute(PluginRepository repository, RepositoryStrategyParameters parameters)
      throws RepositoryStrategyException;
}
