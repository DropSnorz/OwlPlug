package com.owlplug.core.dao;

import com.owlplug.core.model.Plugin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PluginDAO extends CrudRepository<Plugin, Long> {

  Plugin findByPath(String path);
  
  @Transactional
  void deleteByPathContainingIgnoreCase(String path);
}