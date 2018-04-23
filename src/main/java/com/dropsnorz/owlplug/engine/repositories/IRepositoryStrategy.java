package com.dropsnorz.owlplug.engine.repositories;

import com.dropsnorz.owlplug.model.PluginRepository;

public interface IRepositoryStrategy {
	
	public void execute(PluginRepository repository,  RepositoryStrategyParameters parameters);
}
