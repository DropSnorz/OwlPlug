package com.dropsnorz.owlplug.store.dao;

import com.dropsnorz.owlplug.store.model.StaticStoreProduct;
import org.springframework.data.repository.CrudRepository;

public interface StoreProductDAO extends CrudRepository<StaticStoreProduct, Long> {

	public Iterable<StaticStoreProduct> findByNameContainingIgnoreCase(String name);

}
