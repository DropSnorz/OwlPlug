package com.dropsnorz.owlplug.store.model;

import com.dropsnorz.owlplug.core.model.PluginStage;
import com.dropsnorz.owlplug.core.model.PluginType;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class StoreProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	private String pageUrl;
	private String downloadUrl;
	private String screenshotUrl;
	private String creator;
	@Column(columnDefinition = "text")
	private String description;
	private PluginType type;
	private PluginStage stage;

	@ManyToOne
	private Store store;

	@OneToMany(mappedBy = "product", orphanRemoval = true, 
			cascade = { CascadeType.PERSIST, CascadeType.REMOVE})
	private Set<ProductBundle> bundles = new HashSet<>();
	@OneToMany(mappedBy = "product", orphanRemoval = true, 
			fetch = FetchType.EAGER,
			cascade = { CascadeType.PERSIST, CascadeType.REMOVE})
	private Set<ProductTag> tags = new HashSet<>();


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

	public String getScreenshotUrl() {
		return screenshotUrl;
	}

	public void setScreenshotUrl(String screenshotUrl) {
		this.screenshotUrl = screenshotUrl;
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

	public Set<ProductBundle> getBundles() {
		return bundles;
	}

	public void setBundles(Set<ProductBundle> bundles) {
		this.bundles = bundles;
	}

	public Set<ProductTag> getTags() {
		return tags;
	}

	public void setTags(Set<ProductTag> tags) {
		this.tags = tags;
	}

}
