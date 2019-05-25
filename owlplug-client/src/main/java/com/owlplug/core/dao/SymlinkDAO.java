package com.owlplug.core.dao;

import com.owlplug.core.model.Symlink;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface SymlinkDAO extends CrudRepository<Symlink, Long> {
  
  public Symlink findByPath(String path);
  
  @Transactional
  void deleteByPathContainingIgnoreCase(String path);
}
