package com.dropsnorz.owlplug.store.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ProductPlatform {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column
	private String platformTag;
	@ManyToOne
	private StaticStoreProduct product;
	
	public ProductPlatform() {
		
	}
	
	public ProductPlatform(String platformTag, StaticStoreProduct product) {
		this.platformTag = platformTag;
		this.product = product;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPlatformTag() {
		return platformTag;
	}

	public void setPlatformTag(String platformTag) {
		this.platformTag = platformTag;
	}

	public StaticStoreProduct getProduct() {
		return product;
	}

	public void setProduct(StaticStoreProduct product) {
		this.product = product;
	}
	
	

}
