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
 
package com.owlplug.explore.model.search;

import com.owlplug.core.model.PluginType;
import com.owlplug.explore.dao.RemotePackageDAO;
import com.owlplug.explore.model.RemotePackage;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ExploreCriteriaAdapter {

  public static Specification<RemotePackage> toSpecification(List<ExploreFilterCriteria> criteriaList) {

    Specification<RemotePackage> spec = Specification.where(null);
    for (ExploreFilterCriteria criteria : criteriaList) {
      spec = spec.and(toSpecification(criteria));
    }
    return spec;

  }

  public static Specification<RemotePackage> toSpecification(ExploreFilterCriteria criteria) {

    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.NAME)) {
      return RemotePackageDAO.nameContains(String.valueOf(criteria.getValue()));
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.CREATOR)) {
      return RemotePackageDAO.hasCreator(String.valueOf(criteria.getValue()));
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.TAG)) {
      return RemotePackageDAO.hasTag(String.valueOf(criteria.getValue()));
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.TYPE)) {
      return RemotePackageDAO.isTyped((PluginType) criteria.getValue());
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.PLATFORM)) {
      return RemotePackageDAO.hasPlatformTag(String.valueOf(criteria.getValue()));
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.FORMAT)) {
      return RemotePackageDAO.hasFormat(String.valueOf(criteria.getValue()));
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.FORMAT_LIST)) {
      return RemotePackageDAO.hasFormat((List<String>) criteria.getValue());
    }
    return Specification.where(null);

  }

}
