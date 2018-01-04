package com.dropsnorz.owlplug.model;

public class Plugin {
	
	String name;
	String path;
	
	
	public Plugin(){
		
	}
	
	public Plugin(String name, String path){
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return name;
	}
	
	
	
	

}
