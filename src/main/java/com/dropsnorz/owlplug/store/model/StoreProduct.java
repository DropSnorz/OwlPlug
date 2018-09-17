package com.dropsnorz.owlplug.store.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class StoreProduct {
	
	private String name;
	private String pageUrl;
	private String downloadUrl;
	private String iconUrl;
	private String creator;
	private String description;
	
	public StoreProduct() {
		
	}
	

	/**
	 * Creates a new instance.
	 * @param name the product name
	 * @param downloadUrl the product download url
	 * @param iconUrl the product icon/screenshot url
	 * @param creator product creator
	 * @param description product description
	 */
	public StoreProduct(String name, String downloadUrl, String iconUrl, 
			String creator, String description) {
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
	
	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
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
	
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

}
