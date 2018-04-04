package com.dropsnorz.owlplug.model;

import javax.persistence.Entity;

@Entity
public class FileSystemRepository extends Repository {

	protected String remotePath;
	
	public FileSystemRepository() {
		
	}
	
	public FileSystemRepository(String name, String remotePath) {
		super(name);
		
		this.remotePath = remotePath;
		
	}
}
