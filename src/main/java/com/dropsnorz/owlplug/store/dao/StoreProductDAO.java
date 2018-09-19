package com.dropsnorz.owlplug.store.dao;

import com.dropsnorz.owlplug.store.model.StaticStoreProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StoreProductDAO extends CrudRepository<StaticStoreProduct, Long> {
	
	@Query("SELECT product FROM ProductPlatform platform JOIN platform.product product "
			+ "WHERE platform.platformTag=:platformTag")
	public Iterable<StaticStoreProduct> findByPlatform(@Param("platformTag")String platformTag);

	@Query("SELECT product FROM ProductPlatform platform JOIN platform.product product "
			+ "WHERE platform.platformTag=:platformTag "
			+ "AND lower(product.name) LIKE lower(concat('%', :name,'%'))")
	public Iterable<StaticStoreProduct> findByPlatformAndName(
			@Param("platformTag")String platformTag, @Param("name")String name);
	
	@Query("FROM StaticStoreProduct product WHERE product.platforms IS EMPTY "
			+ "AND lower(product.name) LIKE lower(concat('%', :name,'%'))")
	public Iterable<StaticStoreProduct> findProductWithoutPlatformAssignment(@Param("name")String name);
}
