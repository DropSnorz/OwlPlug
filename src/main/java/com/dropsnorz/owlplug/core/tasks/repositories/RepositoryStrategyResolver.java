package com.dropsnorz.owlplug.core.tasks.repositories;

import com.dropsnorz.owlplug.core.model.FileSystemRepository;
import com.dropsnorz.owlplug.core.model.GoogleDriveRepository;
import com.dropsnorz.owlplug.core.model.PluginRepository;
import com.dropsnorz.owlplug.core.tasks.repositories.RepositoryStrategyParameters.RepositoryAction;
import com.dropsnorz.owlplug.core.tasks.repositories.filesystem.FileSystemRepositoryPullingStrategy;
import com.dropsnorz.owlplug.core.tasks.repositories.filesystem.FileSystemRepositoryPushingStrategy;
import com.dropsnorz.owlplug.core.tasks.repositories.googledrive.GoogleDrivePullingStrategy;
import com.dropsnorz.owlplug.core.tasks.repositories.googledrive.GoogleDrivePushingStrategy;
import java.util.HashMap;
import org.springframework.stereotype.Component;

@Component
public class RepositoryStrategyResolver {

	private HashMap<Class, HashMap<RepositoryAction, IRepositoryStrategy>> strategyBindings;


	public RepositoryStrategyResolver() {

		strategyBindings = new HashMap<>();

		// FileSystemRepository bindings
		HashMap<RepositoryAction, IRepositoryStrategy> fileSystemBindings = new HashMap<>();
		fileSystemBindings.put(RepositoryAction.PULL, new FileSystemRepositoryPullingStrategy());
		fileSystemBindings.put(RepositoryAction.PUSH, new FileSystemRepositoryPushingStrategy());

		strategyBindings.put(FileSystemRepository.class, fileSystemBindings);

		// GoogleDrive Bindings
		HashMap<RepositoryAction, IRepositoryStrategy> googleDriveBindings = new HashMap<>();
		googleDriveBindings.put(RepositoryAction.PULL, new GoogleDrivePullingStrategy());
		googleDriveBindings.put(RepositoryAction.PUSH, new GoogleDrivePushingStrategy());

		strategyBindings.put(GoogleDriveRepository.class, googleDriveBindings);
	}

	public IRepositoryStrategy getStrategy(PluginRepository repository, RepositoryStrategyParameters parameters) {

		return strategyBindings.get(repository.getClass()).get(parameters.getRepositoryAction());

	}


	public void execute(PluginRepository repository, RepositoryStrategyParameters parameters) 
			throws RepositoryStrategyException {

		strategyBindings.get(repository.getClass()).get(parameters.getRepositoryAction()).execute(repository, parameters);
	}

}
