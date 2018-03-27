package com.dropsnorz.owlplug.dao;

import org.springframework.data.repository.CrudRepository;

import com.dropsnorz.owlplug.model.Plugin;

public interface RepositoryDAO extends CrudRepository<Plugin, Long> {

}
