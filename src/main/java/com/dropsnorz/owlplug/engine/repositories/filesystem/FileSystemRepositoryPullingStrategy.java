package com.dropsnorz.owlplug.engine.repositories.filesystem;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.dropsnorz.owlplug.engine.repositories.IRepositoryStrategy;
import com.dropsnorz.owlplug.engine.repositories.RepositoryStrategyParameters;
import com.dropsnorz.owlplug.model.FileSystemRepository;
import com.dropsnorz.owlplug.model.PluginRepository;

public class FileSystemRepositoryPullingStrategy implements IRepositoryStrategy {

	@Override
	public void execute(PluginRepository repository, RepositoryStrategyParameters parameters) {
		
		
		FileSystemRepository fileSystemRepository = (FileSystemRepository) repository;	
			
		File sourceDir = new File(fileSystemRepository.getRemotePath());
		File targetDir = new File(parameters.get("target-dir"));
		
		targetDir.mkdirs();
		
		try {
			FileUtils.copyDirectory(sourceDir, targetDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
