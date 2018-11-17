package com.dropsnorz.owlplug.store.model.json;

import java.util.List;

public class ProductJsonMapper {
	
	private String name;
	private String pageUrl;
	private String downloadUrl;
	private String iconUrl;
	private String creator;
	private String description;
	private String type;
	private List<String> platforms;
	private String stage;
	private List<String> tags;
	
	
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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDescription(String version) {
		this.description = version;
	}

	public List<String> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(List<String> platforms) {
		this.platforms = platforms;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
