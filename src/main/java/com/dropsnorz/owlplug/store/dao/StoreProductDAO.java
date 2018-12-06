package com.dropsnorz.owlplug.store.dao;

import com.dropsnorz.owlplug.core.model.PluginType;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;


public interface StoreProductDAO extends CrudRepository<StoreProduct, Long>, JpaSpecificationExecutor<StoreProduct> {


	/**
	 * Store enabled filtering JPA Specification.
	 * @return The JPA specification
	 */
	static Specification<StoreProduct> storeEnabled() {
		return (product, cq, cb) -> cb.equal(product.get("store").get("enabled"), true);
	}

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
			Join<Object, Object> bundles = (Join<Object, Object>) product.fetch("bundles");
			return cb.isMember(platformTag, bundles.get("targets"));
		};
	}

	/**
	 * Platform filtering JPA Specification
	 * Filter products matching the given platformTag or products without platform assignment. 
	 * @param platformTagList - The compatible platformTagList to find
	 * @return The JPA Specification
	 */
	static Specification<StoreProduct> hasPlatformTag(List<String> platformTagList) {
		return (product, cq, cb) -> {
			Join<Object, Object> bundles = (Join<Object, Object>) product.fetch("bundles", JoinType.INNER);
			List<Predicate> predicates = new ArrayList<>();
			for (String platformTag : platformTagList) {
				predicates.add(
						cb.or(
							cb.isMember(platformTag, bundles.get("targets"))
						)
				);
			}
			cq.distinct(true);
			return cb.or(predicates.toArray(new Predicate[predicates.size()]));
		};
	}


	/**
	 * Product tag filtering specification.
	 * Filter products matching the given tags
	 * @param tag - The tag to find
	 * @return The JPA Specification
	 */
	static Specification<StoreProduct> hasTag(String tag) {
		return (product, cq, cb) -> {
			Join<Object, Object> groupPath = product.join("tags", JoinType.INNER);
			return cb.equal(cb.lower(groupPath.get("name")), tag.toLowerCase());

		};
	}

	/**
	 * Product tag filtering specification.
	 * Filter products matching the given tags
	 * @param type - The type to find
	 * @return The JPA Specification
	 */
	static Specification<StoreProduct> isTyped(PluginType type) {
		return (product, cq, cb) -> {
			return cb.equal(product.get("type"), type);

		};
	}


	public Iterable<StoreProduct> findByNameContainingIgnoreCase(String name);

}
