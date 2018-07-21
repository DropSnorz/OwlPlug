package com.dropsnorz.owlplug.store.service;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.dropsnorz.owlplug.store.dao.PluginStoreDAO;
import com.dropsnorz.owlplug.store.dao.StoreProductDAO;
import com.dropsnorz.owlplug.store.model.PluginStore;
import com.dropsnorz.owlplug.store.model.StaticPluginStore;
import com.dropsnorz.owlplug.store.model.StaticStoreProduct;
import com.dropsnorz.owlplug.store.model.StoreProduct;

@Service
public class StoreService {

	@Autowired
	private TaskFactory taskFactory;
	@Autowired
	private PluginStoreDAO pluginStoreDAO;
	@Autowired
	private StoreProductDAO storeProductDAO;


	@PostConstruct
	private void init() {

		PluginStore store = pluginStoreDAO.findByName("OwlPlug Central");
		
		if(store == null) {
			store = new StaticPluginStore();
			store.setName("OwlPlug Central");
		}	
		
		store.setUrl("http://owlplug.dropsnorz.com/store.json");
		pluginStoreDAO.save(store);


	}

	public void syncStores() {

		taskFactory.createStoreSyncTask().run();

	}

	public Iterable<StaticStoreProduct> getStoreProducts() {
		return storeProductDAO.findAll();
	}

	public Iterable<StaticStoreProduct> getStoreProducts(String query) {
		return storeProductDAO.findByNameContainingIgnoreCase(query);
	}

	public void install(StoreProduct product, File targetDirectory) {

		taskFactory.createProductInstallTask(product, targetDirectory).run();
	}


}
