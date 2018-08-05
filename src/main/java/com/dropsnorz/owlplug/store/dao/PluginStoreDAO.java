package com.dropsnorz.owlplug.store.dao;

import com.dropsnorz.owlplug.store.model.PluginStore;
import org.springframework.data.repository.CrudRepository;

public interface PluginStoreDAO extends CrudRepository<PluginStore, Long>  {

	public PluginStore findByName(String name);
}
