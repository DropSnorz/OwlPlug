package com.owlplug.store.dao;

import com.owlplug.store.model.Store;
import org.springframework.data.repository.CrudRepository;

public interface StoreDAO extends CrudRepository<Store, Long> {

  public Store findByName(String name);
}
