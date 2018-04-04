package com.dropsnorz.owlplug.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.dao.FileSystemRepositoryDAO;
import com.dropsnorz.owlplug.dao.RepositoryBaseDAO;
import com.dropsnorz.owlplug.model.FileSystemRepository;
import com.dropsnorz.owlplug.model.Repository;

@Service
public class RepositoryService {
	
	
	@Autowired
	protected FileSystemRepositoryDAO fileSystemRepositoryDAO;
	
	public boolean createRepository(Repository repository){
		
		if(fileSystemRepositoryDAO.findByName(repository.getName()) == null) {
			
			fileSystemRepositoryDAO.save((FileSystemRepository)repository);
			return true;
			
		}
		
		return false;
		
		
			
	}

}
