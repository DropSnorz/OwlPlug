package com.dropsnorz.owlplug.store.dao;

import org.springframework.data.repository.CrudRepository;

import com.dropsnorz.owlplug.store.model.PluginStore;

public interface PluginStoreDAO extends CrudRepository<PluginStore, Long>  {

	public PluginStore findByName(String name);
}
