/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.core.dao;

import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginFormat;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface PluginDAO extends JpaRepository<Plugin, Long>, JpaSpecificationExecutor<Plugin> {

  static Specification<Plugin> nameContains(String name) {
    return (plugin, cq, cb) -> cb.like(cb.lower(plugin.get("name")), "%" + name.toLowerCase() + "%");
  }

  static Specification<Plugin> hasFormat(PluginFormat format) {
    return (plugin, cq, cb) -> cb.equal(plugin.get("format"), format);
  }

  static Specification<Plugin> hasComponentName(String name) {
    return (plugin, cq, cb) -> {
      Join<Object, Object> component = (Join<Object, Object>) plugin.fetch("components", JoinType.INNER);
      return cb.equal(cb.lower(component.get("name")), name.toLowerCase());

    };
  }



  Plugin findByPath(String path);
  
  List<Plugin> findBySyncComplete(boolean syncComplete);
  
  @Transactional
  void deleteByPathContainingIgnoreCase(String path);
}