package com.dropsnorz.owlplug.core.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;

@Entity
@Inheritance
public abstract class Plugin {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	protected String name;
	protected String path;
	protected String bundleId;
	protected String version;
	protected String iconUrl;
	
	protected PluginType type;


	public Plugin(){

	}

	public Plugin(String name, String path) {
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

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public PluginType getType() {
		return type;
	}

	public void setType(PluginType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return name;
	}

	public abstract PluginFormat getFormat();


}
