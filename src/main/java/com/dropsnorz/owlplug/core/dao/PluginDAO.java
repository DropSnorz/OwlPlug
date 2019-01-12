package com.dropsnorz.owlplug.core.dao;

import com.dropsnorz.owlplug.core.model.Plugin;
import org.springframework.data.repository.CrudRepository;

public interface PluginDAO extends CrudRepository<Plugin, Long> {

  Plugin findByPath(String path);
}