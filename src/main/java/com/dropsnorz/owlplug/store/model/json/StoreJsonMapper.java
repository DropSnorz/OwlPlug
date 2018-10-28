package com.dropsnorz.owlplug.store.model.json;

import java.util.List;

public class StoreJsonMapper {
	
	private String name;
	private String url;
	private String type;
	private String version;
	
	private List<ProductJsonMapper> products;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<ProductJsonMapper> getProducts() {
		return products;
	}

	public void setProducts(List<ProductJsonMapper> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "PluginStoreTO [name=" + name + ", url=" + url + ", type=" + type + ", version=" + version
				+ ", products=" + products + "]";
	}
	
	
	
	
	
	

}
