package com.dropsnorz.owlplug.store.dao;

import com.dropsnorz.owlplug.store.model.StoreProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StoreProductDAO extends CrudRepository<StoreProduct, Long> {
	
	@Query("SELECT product FROM ProductPlatform platform JOIN platform.product product "
			+ "WHERE platform.platformTag=:platformTag")
	public Iterable<StoreProduct> findByPlatform(@Param("platformTag")String platformTag);

	@Query("SELECT product FROM ProductPlatform platform JOIN platform.product product "
			+ "WHERE platform.platformTag=:platformTag "
			+ "AND lower(product.name) LIKE lower(concat('%', :name,'%'))")
	public Iterable<StoreProduct> findByPlatformAndName(
			@Param("platformTag")String platformTag, @Param("name")String name);
	
	@Query("FROM StoreProduct product WHERE product.platforms IS EMPTY "
			+ "AND lower(product.name) LIKE lower(concat('%', :name,'%'))")
	public Iterable<StoreProduct> findProductWithoutPlatformAssignment(@Param("name")String name);
	
	public Iterable<StoreProduct> findByNameContainingIgnoreCase(String name);

}
