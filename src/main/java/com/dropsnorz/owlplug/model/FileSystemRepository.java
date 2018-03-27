package com.dropsnorz.owlplug.model;

import javax.persistence.Entity;

@Entity
public class FileSystemRepository extends Repository {

	public FileSystemRepository(String name) {
		super(name);
		
	}
}
