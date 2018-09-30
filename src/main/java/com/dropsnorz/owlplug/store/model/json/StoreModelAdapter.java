package com.dropsnorz.owlplug.store.model.json;

import com.dropsnorz.owlplug.core.model.PluginType;
import com.dropsnorz.owlplug.store.model.PluginStore;
import com.dropsnorz.owlplug.store.model.ProductPlatform;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreModelAdapter {

	private static final Logger log = LoggerFactory.getLogger(StoreModelAdapter.class);


	/**
	 * Crestes a {@link PluginStore} entity from a {@link PluginStoreJsonMapper}.
	 * @param storeJsonMapper pluginStore json mapper
	 * @return pluginStoreEntity
	 */
	public static PluginStore jsonMapperToEntity(PluginStoreJsonMapper storeJsonMapper) {

		PluginStore store = new PluginStore();
		store.setName(storeJsonMapper.getName());
		store.setUrl(storeJsonMapper.getUrl());
		return store;
	}


	/**
	 * Creates a {@link StoreProduct} entity from a {@link ProductJsonMapper}.
	 * @param productJsonMapper product json mapper
	 * @return product entity
	 */
	public static StoreProduct jsonMapperToEntity(ProductJsonMapper productJsonMapper) {

		StoreProduct product = new StoreProduct();
		product.setName(productJsonMapper.getName());
		product.setPageUrl(productJsonMapper.getPageUrl());
		product.setDownloadUrl(productJsonMapper.getDownloadUrl());
		product.setIconUrl(productJsonMapper.getIconUrl());
		product.setCreator(productJsonMapper.getCreator());
		product.setDescription(productJsonMapper.getDescription());
		
		if (productJsonMapper.getType() != null) {
			product.setType(PluginType.fromString(productJsonMapper.getType()));
		}

		ArrayList<ProductPlatform> platforms = new ArrayList<>();
		if (productJsonMapper.getPlatforms() != null) {
			for (String platformTag : productJsonMapper.getPlatforms()) {
				platforms.add(new ProductPlatform(platformTag, product));
			}
		}

		product.setPlatforms(platforms);
		return product;
	}

}
