package com.dropsnorz.owlplug.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginRepository;

@NoRepositoryBean
public interface PluginRepositoryBaseDAO<T extends PluginRepository> extends CrudRepository<T, Long> {
	
    PluginRepository findByName(String name);

}
