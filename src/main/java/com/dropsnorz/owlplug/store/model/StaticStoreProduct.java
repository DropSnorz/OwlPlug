package com.dropsnorz.owlplug.store.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class StaticStoreProduct extends StoreProduct {
	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@ManyToOne
	private PluginStore store;
	
	
	public StaticStoreProduct() {
		super();
	}
	
	
	public StaticStoreProduct(String name, String downloadUrl, String iconUrl, String description, PluginStore store) {
		super(name, downloadUrl, iconUrl, description);
		this.store = store;
	}

}
