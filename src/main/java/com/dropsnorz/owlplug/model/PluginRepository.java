package com.dropsnorz.owlplug.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@Entity
@Inheritance
public abstract class PluginRepository {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	protected String name;
	
	@Transient
	protected List<Plugin> pluginList;
	
	PluginRepository(String name){
		this.name = name;
	}
	
	PluginRepository(){
		
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


	public List<Plugin> getPluginList() {
		return pluginList;
	}

	public void setPluginList(List<Plugin> pluginList) {
		this.pluginList = pluginList;
	}

	@Override
	public String toString() {
		return name;
	}

	
}
