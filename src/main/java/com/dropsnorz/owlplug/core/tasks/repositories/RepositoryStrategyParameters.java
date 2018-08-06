package com.dropsnorz.owlplug.core.tasks.repositories;

import java.util.HashMap;

public class RepositoryStrategyParameters {
	
	protected RepositoryAction repositoryAction;
	protected HashMap<String, String> parameters;
	protected HashMap<String, Object> objects;
	
	
	public RepositoryStrategyParameters() {
		parameters = new HashMap<String, String>();
		objects = new HashMap<String, Object>();
	}

	
	public RepositoryAction getRepositoryAction() {
		return repositoryAction;
	}


	public void setRepositoryAction(RepositoryAction repositoryAction) {
		this.repositoryAction = repositoryAction;
	}
	
	public String get(String key) {
		return parameters.get(key);
	}
	
	public void put(String key, String value) {
		parameters.put(key, value);
	}
	
	public Object getOject(String key) {
		return objects.get(key);
	}
	
	public void putObject(String key, Object value) {
		objects.put(key, value);
	}

	public enum RepositoryAction{
		INIT,
		PULL,
		PUSH,
		CLEAN
	}

}
