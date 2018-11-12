package com.dropsnorz.owlplug.store.model;

import com.dropsnorz.owlplug.core.model.PluginStage;
import com.dropsnorz.owlplug.core.model.PluginType;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class StoreProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	private String pageUrl;
	private String downloadUrl;
	private String iconUrl;
	private String creator;
	private String description;
	private PluginType type;
	private PluginStage stage;

	@ManyToOne
	private Store store;

	@OneToMany(mappedBy = "product", orphanRemoval = true, 
			cascade = { CascadeType.PERSIST, CascadeType.REMOVE})
	private List<ProductPlatform> platforms;


	public StoreProduct() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public PluginType getType() {
		return type;
	}

	public void setType(PluginType type) {
		this.type = type;
	}
	
	public PluginStage getStage() {
		return stage;
	}

	public void setStage(PluginStage stage) {
		this.stage = stage;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public List<ProductPlatform> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(List<ProductPlatform> platforms) {
		this.platforms = platforms;
	}

}
