package com.dropsnorz.owlplug.core.engine.repositories;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyParameters.RepositoryAction;
import com.dropsnorz.owlplug.core.engine.repositories.filesystem.FileSystemRepositoryPullingStrategy;
import com.dropsnorz.owlplug.core.engine.repositories.filesystem.FileSystemRepositoryPushingStrategy;
import com.dropsnorz.owlplug.core.engine.repositories.googledrive.GoogleDrivePullingStrategy;
import com.dropsnorz.owlplug.core.engine.repositories.googledrive.GoogleDrivePushingStrategy;
import com.dropsnorz.owlplug.core.model.FileSystemRepository;
import com.dropsnorz.owlplug.core.model.GoogleDriveRepository;
import com.dropsnorz.owlplug.core.model.PluginRepository;

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
		
		// GoogleDrive Bindings
		HashMap<RepositoryAction, IRepositoryStrategy> googleDriveBindings = new HashMap<RepositoryAction, IRepositoryStrategy>();
		googleDriveBindings.put(RepositoryAction.PULL, new GoogleDrivePullingStrategy());
		googleDriveBindings.put(RepositoryAction.PUSH, new GoogleDrivePushingStrategy());

		strategyBindings.put(GoogleDriveRepository.class, googleDriveBindings);
	}
	
	public IRepositoryStrategy getStrategy(PluginRepository repository, RepositoryStrategyParameters parameters) {
		
		return strategyBindings.get(repository.getClass()).get(parameters.getRepositoryAction());
	
	}
	
	
	public void execute(PluginRepository repository, RepositoryStrategyParameters parameters) {
		
		 try {
			strategyBindings.get(repository.getClass()).get(parameters.getRepositoryAction()).execute(repository, parameters);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	
	}

}
