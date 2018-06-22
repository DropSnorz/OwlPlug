package com.dropsnorz.owlplug.store.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.store.dao.PluginStoreDAO;
import com.dropsnorz.owlplug.store.dao.StoreProductDAO;
import com.dropsnorz.owlplug.store.model.PluginStore;
import com.dropsnorz.owlplug.store.model.StaticPluginStore;
import com.dropsnorz.owlplug.store.model.StaticStoreProduct;
import com.dropsnorz.owlplug.store.model.StoreProduct;

@Service
public class StoreService {
	
	@Autowired
	private PluginStoreDAO pluginStoreDAO;
	
	@Autowired
	private StoreProductDAO storeProductDAO;
	
	StoreService(){

	}
	
	@PostConstruct
	private void init() {
		storeProductDAO.deleteAll();
		pluginStoreDAO.deleteAll();
		PluginStore store = new StaticPluginStore();
		pluginStoreDAO.save(store);
		
		StaticStoreProduct product1 = new StaticStoreProduct("WobbleIzer","","http://owlplug.dropsnorz.com/store/products/com.dropsnorz.wobbleizer/wobbleizer.png","", store);
		StaticStoreProduct product2 = new StaticStoreProduct("WobbleIzer","","http://owlplug.dropsnorz.com/store/products/com.dropsnorz.wobbleizer/wobbleizer.png","", store);
		StaticStoreProduct product3 = new StaticStoreProduct("WobbleIzer","","http://owlplug.dropsnorz.com/store/products/com.dropsnorz.wobbleizer/wobbleizer.png","", store);
		
		storeProductDAO.save(product1);
		storeProductDAO.save(product2);
		storeProductDAO.save(product3);
	}
	
	public void updateStaticStores() {
		
	}

	public Iterable<StaticStoreProduct> getStoreProducts() {
		return storeProductDAO.findAll();
	}

	public List<StoreProduct> getStoreProducts(String query) {
		return new ArrayList<StoreProduct>();
	}


}
