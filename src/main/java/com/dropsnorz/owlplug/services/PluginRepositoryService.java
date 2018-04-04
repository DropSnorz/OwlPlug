package com.dropsnorz.owlplug.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.dao.FileSystemRepositoryDAO;
import com.dropsnorz.owlplug.dao.PluginRepositoryBaseDAO;
import com.dropsnorz.owlplug.model.FileSystemRepository;
import com.dropsnorz.owlplug.model.PluginRepository;

@Service
public class PluginRepositoryService {
	
	
	@Autowired
	protected FileSystemRepositoryDAO fileSystemRepositoryDAO;
	
	public boolean createRepository(PluginRepository repository){
		
		if(fileSystemRepositoryDAO.findByName(repository.getName()) == null) {
			
			fileSystemRepositoryDAO.save((FileSystemRepository)repository);
			return true;
			
		}
		
		return false;
		
		
			
	}

}
