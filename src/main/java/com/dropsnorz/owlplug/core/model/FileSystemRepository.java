package com.dropsnorz.owlplug.core.model;

import javax.persistence.Entity;

@Entity
public class FileSystemRepository extends PluginRepository {

	protected String remotePath;
	
	public FileSystemRepository() {
		
	}
	
	public FileSystemRepository(String name, String remotePath) {
		super(name);
		
		this.remotePath = remotePath;
		
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	
	
}
