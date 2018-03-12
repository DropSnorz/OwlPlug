package com.dropsnorz.owlplug.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dropsnorz.owlplug.model.Plugin;

public interface PluginDAO extends CrudRepository<Plugin, Long> {

    Plugin findByPath(String path);
}