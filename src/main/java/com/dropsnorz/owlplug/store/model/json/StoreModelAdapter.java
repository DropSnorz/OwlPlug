package com.dropsnorz.owlplug.store.model.json;

import com.dropsnorz.owlplug.core.model.PluginStage;
import com.dropsnorz.owlplug.core.model.PluginType;
import com.dropsnorz.owlplug.core.utils.UrlUtils;
import com.dropsnorz.owlplug.store.model.ProductPlatform;
import com.dropsnorz.owlplug.store.model.ProductTag;
import com.dropsnorz.owlplug.store.model.Store;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import java.util.HashSet;

public class StoreModelAdapter {


	/**
	 * Crestes a {@link Store} entity from a {@link StoreJsonMapper}.
	 * @param storeJsonMapper pluginStore json mapper
	 * @return pluginStoreEntity
	 */
	public static Store jsonMapperToEntity(StoreJsonMapper storeJsonMapper) {

		Store store = new Store();
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
		product.setDownloadUrl(UrlUtils.fixSpaces(productJsonMapper.getDownloadUrl()));
		product.setIconUrl(UrlUtils.fixSpaces(productJsonMapper.getIconUrl()));
		product.setCreator(productJsonMapper.getCreator());
		product.setDescription(productJsonMapper.getDescription());
		
		
		if (productJsonMapper.getType() != null) {
			product.setType(PluginType.fromString(productJsonMapper.getType()));
		}
		
		if (productJsonMapper.getStage() != null) {
			product.setStage(PluginStage.fromString(productJsonMapper.getStage()));
		}

		HashSet<ProductPlatform> platforms = new HashSet<>();
		if (productJsonMapper.getPlatforms() != null) {
			for (String platformTag : productJsonMapper.getPlatforms()) {
				platforms.add(new ProductPlatform(platformTag, product));
			}
		}
		product.setPlatforms(platforms);
		
		HashSet<ProductTag> tags = new HashSet<>();
		if (productJsonMapper.getTags() != null) {
			for (String tag : productJsonMapper.getTags()) {
				tags.add(new ProductTag(tag, product));
			}
		}
		product.setTags(tags);
		
		return product;
	}

}
