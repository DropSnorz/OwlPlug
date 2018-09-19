package com.dropsnorz.owlplug.store.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class StaticStoreProduct extends StoreProduct {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	private PluginStore store;

	@OneToMany(mappedBy = "product", orphanRemoval = true, 
			cascade = { CascadeType.PERSIST, CascadeType.REMOVE})
	private List<ProductPlatform> platforms;


	public StaticStoreProduct() {
		super();
	}


	public StaticStoreProduct(String name, String downloadUrl, String iconUrl, String creator, 
			String description, PluginStore store) {
		super(name, downloadUrl, iconUrl, creator, description);
		this.store = store;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PluginStore getStore() {
		return store;
	}

	public void setStore(PluginStore store) {
		this.store = store;
	}

	public List<ProductPlatform> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(List<ProductPlatform> platforms) {
		this.platforms = platforms;
	}
	
}
