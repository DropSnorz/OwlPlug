package com.owlplug.core.dao;

import com.owlplug.core.model.Symlink;
import org.springframework.data.repository.CrudRepository;

public interface SymlinkDAO extends CrudRepository<Symlink, Long> {
  
  public Symlink findByPath(String path);
}
