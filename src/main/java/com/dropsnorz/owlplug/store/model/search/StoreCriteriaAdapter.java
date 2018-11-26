package com.dropsnorz.owlplug.store.model.search;

import com.dropsnorz.owlplug.store.dao.StoreProductDAO;
import com.dropsnorz.owlplug.store.model.StoreProduct;
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
			return StoreProductDAO.nameContains(criteria.getValue());
		}
		return Specification.where(null);

	}

}
