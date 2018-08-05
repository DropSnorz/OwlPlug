package com.dropsnorz.owlplug.core.dao;

import com.dropsnorz.owlplug.core.model.PluginRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PluginRepositoryBaseDAO<T extends PluginRepository> extends CrudRepository<T, Long> {

	PluginRepository findByName(String name);

}
