package com.dropsnorz.owlplug.core.model;

import javax.persistence.Entity;

@Entity
public class VST3Plugin extends Plugin {

	public VST3Plugin() {
		super();
	}
	public VST3Plugin(String name, String path){
		super(name, path);
	}
	
	@Override
	public PluginType getType() {
		return PluginType.VST3;
	}
	
}