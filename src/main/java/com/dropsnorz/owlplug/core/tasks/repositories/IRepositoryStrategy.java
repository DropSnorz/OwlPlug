package com.dropsnorz.owlplug.core.tasks.repositories;

import com.dropsnorz.owlplug.core.model.PluginRepository;

public interface IRepositoryStrategy {
	
	public void execute(PluginRepository repository,  RepositoryStrategyParameters parameters) 
			throws RepositoryStrategyException;
}
