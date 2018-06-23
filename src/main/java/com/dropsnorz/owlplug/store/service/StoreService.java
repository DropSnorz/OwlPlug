package com.dropsnorz.owlplug.store.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.dropsnorz.owlplug.store.controllers.StoreController;
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
	@Autowired
	private StoreController storeController;

	StoreService(){

	}

	@PostConstruct
	private void init() {
		storeProductDAO.deleteAll();
		pluginStoreDAO.deleteAll();
		PluginStore store = new StaticPluginStore();
		store.setName("OwlPlug Central");
		store.setUrl("http://owlplug.dropsnorz.com/store/store.json");
		pluginStoreDAO.save(store);

	}

	public void syncStores() {

		taskFactory.createStoreSyncTask().getTask().run();
		storeController.refreshView();

	}

	public Iterable<StaticStoreProduct> getStoreProducts() {
		return storeProductDAO.findAll();
	}

	public List<StoreProduct> getStoreProducts(String query) {
		return new ArrayList<StoreProduct>();
	}


}
