package com.dropsnorz.owlplug.core.tasks.repositories.filesystem;


import com.dropsnorz.owlplug.core.model.FileSystemRepository;
import com.dropsnorz.owlplug.core.model.PluginRepository;
import com.dropsnorz.owlplug.core.tasks.repositories.IRepositoryStrategy;
import com.dropsnorz.owlplug.core.tasks.repositories.RepositoryStrategyException;
import com.dropsnorz.owlplug.core.tasks.repositories.RepositoryStrategyParameters;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class FileSystemRepositoryPullingStrategy implements IRepositoryStrategy {

	@Override
	public void execute(PluginRepository repository, RepositoryStrategyParameters parameters) 
			throws RepositoryStrategyException {
		
		FileSystemRepository fileSystemRepository = (FileSystemRepository) repository;	
		File sourceDir = new File(fileSystemRepository.getRemotePath());
		File targetDir = new File(parameters.get("target-dir"));
		targetDir.mkdirs();
		try {
			FileUtils.copyDirectory(sourceDir, targetDir);
		} catch (IOException e) {
			
			throw new RepositoryStrategyException("Pulling repository " + fileSystemRepository.getName() 
				+ " - Error during file pulling", e);
		}
	}
}
