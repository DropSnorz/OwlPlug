package com.dropsnorz.owlplug.store.dao;

import org.springframework.data.repository.CrudRepository;

import com.dropsnorz.owlplug.store.model.PluginStore;
import com.dropsnorz.owlplug.store.model.StaticStoreProduct;

public interface StoreProductDAO extends CrudRepository<StaticStoreProduct, Long> {

}
