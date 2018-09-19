package com.dropsnorz.owlplug.store.dao;

import com.dropsnorz.owlplug.store.model.ProductPlatform;
import org.springframework.data.repository.CrudRepository;

public interface ProductPlatformDAO extends CrudRepository<ProductPlatform, Long> {
	
	public ProductPlatform findByPlatformTag(String platformTag);

}
