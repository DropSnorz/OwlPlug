package com.dropsnorz.owlplug.store.model.json;

import com.dropsnorz.owlplug.store.model.PluginStore;
import com.dropsnorz.owlplug.store.model.StaticStoreProduct;

public class StoreModelConverter {
	
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
		product.setDescription(productTO.getDescription());
		
		return product;
	}

}
