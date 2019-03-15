package com.owlplug.core.dao;

import com.owlplug.core.model.Plugin;
import org.springframework.data.repository.CrudRepository;

public interface PluginDAO extends CrudRepository<Plugin, Long> {

  Plugin findByPath(String path);
}