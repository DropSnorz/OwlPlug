package com.dropsnorz.owlplug.core.engine.repositories;

import java.util.HashMap;

public class RepositoryStrategyParameters {
	
	protected RepositoryAction repositoryAction;
	protected HashMap<String, String> parameters;
	
	
	public RepositoryStrategyParameters() {
		parameters = new HashMap<String, String>();
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

	public enum RepositoryAction{
		INIT,
		PULL,
		PUSH,
		CLEAN
	}

}
