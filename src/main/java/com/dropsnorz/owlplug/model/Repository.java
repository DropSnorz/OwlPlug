package com.dropsnorz.owlplug.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.MappedSuperclass;

@Entity
@Inheritance
public abstract class Repository {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	protected String name;
	protected String localPath;
	
	Repository(String name){
		this.name = name;
	}
	
	Repository(){
		
	}
	
	
	
	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getLocalPath() {
		return localPath;
	}



	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}


	@Override
	public String toString() {
		return name;
	}

	
}
