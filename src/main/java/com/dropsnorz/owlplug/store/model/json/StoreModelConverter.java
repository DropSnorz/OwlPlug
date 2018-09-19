package com.dropsnorz.owlplug.store.model.json;

import com.dropsnorz.owlplug.store.model.PluginStore;
import com.dropsnorz.owlplug.store.model.ProductPlatform;
import com.dropsnorz.owlplug.store.model.StaticStoreProduct;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreModelConverter {
	private static final Logger log = LoggerFactory.getLogger(StoreModelConverter.class);


	public static PluginStore fromTO(PluginStoreTO storeTO) {

		PluginStore store = new PluginStore();
		store.setName(storeTO.getName());
		store.setUrl(storeTO.getUrl());

		return store;

	}

	public static StaticStoreProduct fromTO(ProductTO productTO) {

		StaticStoreProduct product = new StaticStoreProduct();
		product.setName(productTO.getName());
		product.setPageUrl(productTO.getPageUrl());
		product.setDownloadUrl(productTO.getDownloadUrl());
		product.setIconUrl(productTO.getIconUrl());
		product.setCreator(productTO.getCreator());
		product.setDescription(productTO.getDescription());

		ArrayList<ProductPlatform> platforms = new ArrayList<>();
		if (productTO.getPlatforms() != null) {
			for (String platformTag : productTO.getPlatforms()) {
				platforms.add(new ProductPlatform(platformTag, product));
			}
		}

		product.setPlatforms(platforms);
		return product;
	}

}
