package com.dropsnorz.owlplug.store.dao;

import com.dropsnorz.owlplug.store.model.StoreProduct;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;


public interface StoreProductDAO extends CrudRepository<StoreProduct, Long>, JpaSpecificationExecutor<StoreProduct> {
	
	
	/**
	 * Name filtering JPA Specification.
	 * @param name - The product name
	 * @return The JPA specification
	 */
	static Specification<StoreProduct> nameContains(String name) {
		return (product, cq, cb) -> cb.like(cb.lower(product.get("name")), "%" + name.toLowerCase() + "%");
	}
	
	/**
	 * Platform filtering JPA Specification
	 * Filter products matching the given platformTag or products without platform assignment. 
	 * @param platformTag - The platformTag to find
	 * @return The JPA Specification
	 */
	static Specification<StoreProduct> hasPlatformTag(String platformTag) {
		return (product, cq, cb) -> {
			Join<Object, Object> groupPath = product.join("platforms", JoinType.LEFT);
			return cb.or(cb.isEmpty(product.get("platforms")), cb.equal(groupPath.get("platformTag"), platformTag));
		};
	}
	
	
	public Iterable<StoreProduct> findByNameContainingIgnoreCase(String name);

}
