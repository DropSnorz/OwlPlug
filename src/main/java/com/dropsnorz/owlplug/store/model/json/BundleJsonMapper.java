package com.dropsnorz.owlplug.store.model.json;

import java.util.List;

public class BundleJsonMapper {
	
	private String name;
	private List<String> targets;
	private String downloadUrl;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getTargets() {
		return targets;
	}
	
	public void setTargets(List<String> targets) {
		this.targets = targets;
	}
	
	public String getDownloadUrl() {
		return downloadUrl;
	}
	
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	
	
	
	

}
