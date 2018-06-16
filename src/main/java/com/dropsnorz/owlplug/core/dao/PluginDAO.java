package com.dropsnorz.owlplug.core.dao;

import org.springframework.data.repository.CrudRepository;

import com.dropsnorz.owlplug.core.model.Plugin;

public interface PluginDAO extends CrudRepository<Plugin, Long> {

    Plugin findByPath(String path);
}