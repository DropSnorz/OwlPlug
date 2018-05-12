package com.dropsnorz.owlplug.core.engine.repositories;

import com.dropsnorz.owlplug.core.model.PluginRepository;

public interface IRepositoryStrategy {
	
	public void execute(PluginRepository repository,  RepositoryStrategyParameters parameters);
}
