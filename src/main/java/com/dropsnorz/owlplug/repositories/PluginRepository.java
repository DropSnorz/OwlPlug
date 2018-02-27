package com.dropsnorz.owlplug.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dropsnorz.owlplug.model.Plugin;

public interface PluginRepository extends CrudRepository<Plugin, Long> {

    Plugin findByPath(String path);
}