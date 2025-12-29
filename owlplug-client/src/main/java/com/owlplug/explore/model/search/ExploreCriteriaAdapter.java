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

import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.repositories.RemotePackageRepository;
import com.owlplug.plugin.model.PluginType;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ExploreCriteriaAdapter {

  public static Specification<RemotePackage> toSpecification(List<ExploreFilterCriteria> criteriaList) {

    Specification<RemotePackage> spec = Specification.unrestricted();
    for (ExploreFilterCriteria criteria : criteriaList) {
      spec = spec.and(toSpecification(criteria));
    }
    return spec;

  }

  public static Specification<RemotePackage> toSpecification(ExploreFilterCriteria criteria) {

    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.NAME)) {
      return RemotePackageRepository.nameContains(String.valueOf(criteria.getValue()));
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.CREATOR)) {
      return RemotePackageRepository.hasCreator(String.valueOf(criteria.getValue()));
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.TAG)) {
      return RemotePackageRepository.hasTag(String.valueOf(criteria.getValue()));
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.TYPE)) {
      return RemotePackageRepository.isTyped((PluginType) criteria.getValue());
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.PLATFORM)) {
      return RemotePackageRepository.hasPlatformTag(String.valueOf(criteria.getValue()));
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.PLATFORM_LIST)) {
      return RemotePackageRepository.hasPlatformTag((List<String>) criteria.getValue());
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.FORMAT)) {
      return RemotePackageRepository.hasFormat(String.valueOf(criteria.getValue()));
    }
    if (criteria.getFilterType().equals(ExploreFilterCriteriaType.FORMAT_LIST)) {
      return RemotePackageRepository.hasFormat((List<String>) criteria.getValue());
    }

    return Specification.unrestricted();

  }

}
