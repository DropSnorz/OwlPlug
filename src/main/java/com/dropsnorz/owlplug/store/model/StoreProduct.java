package com.dropsnorz.owlplug.store.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class StoreProduct {
	
	private String name;
	private String downloadUrl;
	private String iconUrl;
	private String description;
	
	public StoreProduct() {
		
	}
	
	public StoreProduct(String name, String downloadUrl, String iconUrl, String description) {
		super();
		this.name = name;
		this.downloadUrl = downloadUrl;
		this.iconUrl = iconUrl;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
