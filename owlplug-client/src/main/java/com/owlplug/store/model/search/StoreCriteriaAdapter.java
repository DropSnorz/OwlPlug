/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.store.model.search;

import com.owlplug.core.model.PluginType;
import com.owlplug.store.dao.StoreProductDAO;
import com.owlplug.store.model.StoreProduct;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class StoreCriteriaAdapter {

  public static Specification<StoreProduct> toSpecification(List<StoreFilterCriteria> criteriaList) {

    Specification<StoreProduct> spec = Specification.where(null);
    for (StoreFilterCriteria criteria : criteriaList) {
      spec = spec.and(toSpecification(criteria));
    }
    return spec;

  }

  public static Specification<StoreProduct> toSpecification(StoreFilterCriteria criteria) {

    if (criteria.getFilterType().equals(StoreFilterCriteriaType.NAME)) {
      return StoreProductDAO.nameContains(String.valueOf(criteria.getValue()));
    }
    if (criteria.getFilterType().equals(StoreFilterCriteriaType.TAG)) {
      return StoreProductDAO.hasTag(String.valueOf(criteria.getValue()));
    }
    if (criteria.getFilterType().equals(StoreFilterCriteriaType.TYPE)) {
      return StoreProductDAO.isTyped((PluginType) criteria.getValue());
    }
    if (criteria.getFilterType().equals(StoreFilterCriteriaType.PLATFORM)) {
      return StoreProductDAO.hasPlatformTag(String.valueOf(criteria.getValue()));
    }
    return Specification.where(null);

  }

}
