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
 
package com.owlplug.explore.dao;

import com.owlplug.core.model.PluginType;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RemotePackageDAO extends CrudRepository<RemotePackage, Long>, JpaSpecificationExecutor<RemotePackage> {

  /**
   * Store enabled filtering JPA Specification.
   * 
   * @return The JPA specification
   */
  static Specification<RemotePackage> sourceEnabled() {
    return (remotePackage, cq, cb) -> cb.equal(remotePackage.get("remoteSource").get("enabled"), true);
  }

  /**
   * Name filtering JPA Specification.
   * 
   * @param name - The product name
   * @return The JPA specification
   */
  static Specification<RemotePackage> nameContains(String name) {
    return (remotePackage, cq, cb) -> cb.like(cb.lower(remotePackage.get("name")), "%" + name.toLowerCase() + "%");
  }
  
  /**
   * Creator name filtering JPA Specification.
   * @param creator - The creator name
   * @return The JPA Specification
   */
  static Specification<RemotePackage> hasCreator(String creator) {
    return (remotePackage, cq, cb) -> cb.equal(remotePackage.get("creator"), creator);
  }


  /**
   * Bundle format filtering JPA Specification to filter packages matching the given format.
   *
   * @param format - The platformTag to find
   * @return The JPA Specification
   */
  @SuppressWarnings("unchecked")
  static Specification<RemotePackage> hasFormat(String format) {
    return (remotePackage, cq, cb) -> {
      Join<Object, Object> bundles = (Join<Object, Object>) remotePackage.fetch("bundles", JoinType.INNER);
      return bundles.join("formats").in(format);
    };
  }

  /**
   * Platform filtering JPA Specification Filter packages matching the given
   * platformTag or packages without platform assignment.
   *
   * @param formatList - The compatible platformTagList to find
   * @return The JPA Specification
   */
  @SuppressWarnings("unchecked")
  static Specification<RemotePackage> hasFormat(List<String> formatList) {
    return (remotePackage, cq, cb) -> {
      Join<Object, Object> bundles = (Join<Object, Object>) remotePackage.fetch("bundles", JoinType.INNER);
      return bundles.join("formats").in(formatList);

    };
  }

  /**
   * Platform filtering JPA Specification Filter packages matching the given platformTag.
   * 
   * @param platformTag - The platformTag to find
   * @return The JPA Specification
   */
  @SuppressWarnings("unchecked")
  static Specification<RemotePackage> hasPlatformTag(String platformTag) {
    return (remotePackage, cq, cb) -> {
      Join<Object, Object> bundles = (Join<Object, Object>) remotePackage.fetch("bundles", JoinType.INNER);
      return bundles.join("targets").in(platformTag);
    };
  }

  /**
   * Platform filtering JPA Specification Filter packages matching the given
   * platformTag or packages without platform assignment.
   * 
   * @param platformTagList - The compatible platformTagList to find
   * @return The JPA Specification
   */
  @SuppressWarnings("unchecked")
  static Specification<RemotePackage> hasPlatformTag(List<String> platformTagList) {
    return (remotePackage, cq, cb) -> {
      Join<Object, Object> bundles = (Join<Object, Object>) remotePackage.fetch("bundles", JoinType.INNER);
      return bundles.join("targets").in(platformTagList);

    };
  }

  /**
   * Product tag filtering specification. Filter packages matching the given tags
   * 
   * @param tag - The tag to find
   * @return The JPA Specification
   */
  @SuppressWarnings("unchecked")
  static Specification<RemotePackage> hasTag(String tag) {
    return (remotePackage, cq, cb) -> {
      Join<Object, Object> groupPath = (Join<Object, Object>) remotePackage.fetch("tags", JoinType.INNER);
      return cb.equal(cb.lower(groupPath.get("name")), tag.toLowerCase());

    };
  }

  /**
   * Product tag filtering specification. Filter packages matching the given tags
   * 
   * @param type - The type to find
   * @return The JPA Specification
   */
  static Specification<RemotePackage> isTyped(PluginType type) {
    return (remotePackage, cq, cb) -> {
      return cb.equal(remotePackage.get("type"), type);

    };
  }

  public Iterable<RemotePackage> findByNameContainingIgnoreCase(String name);
  
  @Query("SELECT DISTINCT p.creator FROM RemotePackage p")
  public List<String> findDistinctCreators();
  

}
