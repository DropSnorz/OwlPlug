package com.dropsnorz.owlplug.store.service;

import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.dropsnorz.owlplug.core.model.OSType;
import com.dropsnorz.owlplug.store.dao.StoreDAO;
import com.dropsnorz.owlplug.store.dao.StoreProductDAO;
import com.dropsnorz.owlplug.store.model.Store;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.dropsnorz.owlplug.store.model.json.StoreJsonMapper;
import com.dropsnorz.owlplug.store.model.json.StoreModelAdapter;
import com.dropsnorz.owlplug.store.tasks.ProductInstallTask;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ApplicationDefaults applicationDefaults;
	@Autowired
	private TaskFactory taskFactory;
	@Autowired
	private StoreDAO pluginStoreDAO;
	@Autowired
	private StoreProductDAO storeProductDAO;


	@PostConstruct
	private void init() {

		Store store = pluginStoreDAO.findByName("OwlPlug Central");

		if (store == null) {
			store = new Store();
			store.setName("OwlPlug Central");
		}	
		store.setApiUrl("http://owlplug.dropsnorz.com/store.json");
		store.setUrl("http://owlplug.dropsnorz.com/store.json");

		pluginStoreDAO.save(store);


	}

	public void syncStores() {
		taskFactory.createStoreSyncTask().schedule();
	}


	/**
	 * Retrieves all products from stores which are compatible with the current platform.
	 * @return list of store products
	 */
	public Iterable<StoreProduct> getStoreProducts() {
		OSType osType = applicationDefaults.getPlatform();
		String platformTag = osType.getCode();

		return storeProductDAO.findAll(StoreProductDAO.hasPlatformTag(platformTag));
	}

	/**
	 * Retrieves products from store with name matching the given parameters and 
	 * compatible with the current platform.
	 * @param name part of the plugin name
	 * @return
	 */
	public Iterable<StoreProduct> getStoreProducts(String name) {
		OSType osType = applicationDefaults.getPlatform();
		String platformTag = osType.getCode();
		
		return storeProductDAO.findAll(
				StoreProductDAO.nameContains(name).and(
						StoreProductDAO.hasPlatformTag(platformTag)));
	}

	public Iterable<StoreProduct> getProductsByName(String name) {
		return storeProductDAO.findByNameContainingIgnoreCase(name);
	}

	/**
	 * Downloads and installs a store product in a directory.
	 * @param product - store product to retrieve
	 * @param targetDirectory - directory where the product will be installed
	 */
	public void install(StoreProduct product, File targetDirectory) {		
		taskFactory.create(new ProductInstallTask(product, targetDirectory, applicationDefaults))
			.setOnSucceeded(e -> taskFactory.createPluginSyncTask().scheduleNow())
			.schedule();
	}

	/**
	 * Creates a PluginStore instance requesting a store url endpoint.
	 * @param url Store endpoint url
	 * @return created pluginstore instance, null if an error occurs
	 */
	public Store getPluginStoreFromUrl(String url) {

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(httpGet);

			HttpEntity entity = response.getEntity();
			ObjectMapper objectMapper = new ObjectMapper()
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			try {
				StoreJsonMapper pluginStoreTO = objectMapper.readValue(entity.getContent(), StoreJsonMapper.class);
				EntityUtils.consume(entity);
				Store store = StoreModelAdapter.jsonMapperToEntity(pluginStoreTO);
				store.setApiUrl(url);
				return store;
			} catch (Exception e) {
				log.error("Error parsing store response: " + url, e);
				return null;
				
			} finally {
				response.close();
			}

		} catch (IOException e) {
			log.error("Error accessing store: Check your network connectivity", e);
			return null;
		}

	}

}
