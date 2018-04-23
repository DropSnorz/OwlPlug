package com.dropsnorz.owlplug.engine.repositories;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.dropsnorz.owlplug.engine.repositories.RepositoryStrategyParameters.RepositoryAction;
import com.dropsnorz.owlplug.engine.repositories.filesystem.FileSystemRepositoryPullingStrategy;
import com.dropsnorz.owlplug.engine.repositories.filesystem.FileSystemRepositoryPushingStrategy;
import com.dropsnorz.owlplug.model.FileSystemRepository;
import com.dropsnorz.owlplug.model.PluginRepository;

@Component
public class RepositoryStrategyResolver {
	
	HashMap<Class, HashMap<RepositoryAction, IRepositoryStrategy>> strategyBindings;
	
	
	public RepositoryStrategyResolver() {
		
		strategyBindings = new HashMap<Class, HashMap<RepositoryAction, IRepositoryStrategy>>();
		
		// FileSystemRepository bindings
		HashMap<RepositoryAction, IRepositoryStrategy> fileSystemBindings = new HashMap<RepositoryAction, IRepositoryStrategy>();
		fileSystemBindings.put(RepositoryAction.PULL, new FileSystemRepositoryPullingStrategy());
		fileSystemBindings.put(RepositoryAction.PUSH, new FileSystemRepositoryPushingStrategy());

		strategyBindings.put(FileSystemRepository.class, fileSystemBindings);
	}
	
	public IRepositoryStrategy getStrategy(PluginRepository repository, RepositoryStrategyParameters parameters) {
		
		return strategyBindings.get(repository.getClass()).get(parameters.getRepositoryAction());
	
	}
	
	
	public void execute(PluginRepository repository, RepositoryStrategyParameters parameters) {
		
		 strategyBindings.get(repository.getClass()).get(parameters.getRepositoryAction()).execute(repository, parameters);;
	
	}

}
