package com.dropsnorz.owlplug.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.Repository;

@NoRepositoryBean
public interface RepositoryBaseDAO<T extends Repository> extends CrudRepository<T, Long> {
	
    Repository findByName(String name);

}
