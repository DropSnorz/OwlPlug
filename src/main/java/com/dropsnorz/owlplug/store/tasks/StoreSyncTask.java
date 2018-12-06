package com.dropsnorz.owlplug.store.tasks;

import com.dropsnorz.owlplug.core.tasks.AbstractTask;
import com.dropsnorz.owlplug.core.tasks.TaskException;
import com.dropsnorz.owlplug.core.tasks.TaskResult;
import com.dropsnorz.owlplug.store.dao.StoreDAO;
import com.dropsnorz.owlplug.store.dao.StoreProductDAO;
import com.dropsnorz.owlplug.store.model.Store;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.dropsnorz.owlplug.store.model.json.ProductJsonMapper;
import com.dropsnorz.owlplug.store.model.json.StoreJsonMapper;
import com.dropsnorz.owlplug.store.model.json.StoreModelAdapter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreSyncTask extends AbstractTask {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private StoreDAO pluginStoreDAO;
	private StoreProductDAO storeProductDAO;

	/**
	 * Creates a new StoreSync tasks.
	 * @param pluginStoreDAO pluginStore DAO
	 * @param storeProductDAO storeProduct DAO
	 */
	public StoreSyncTask(StoreDAO pluginStoreDAO, StoreProductDAO storeProductDAO) {
		super("Sync plugin stores");
		this.pluginStoreDAO = pluginStoreDAO;
		this.storeProductDAO = storeProductDAO;
	}


	@Override
	protected TaskResult call() throws TaskException {

		this.updateProgress(0, 1);
		this.updateMessage("Sync plugins stores");
		
		Iterable<Store> storeList = pluginStoreDAO.findAll();
		this.setMaxProgress(2 + Iterables.size(storeList));
		
		storeProductDAO.deleteAll();

		this.commitProgress(1);
		CloseableHttpResponse response = null;

		for (Store store : pluginStoreDAO.findAll()) {
			try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
				log.debug("Exploring store {}", store.getName());
				this.updateMessage("Exploring store " + store.getName());
				HttpGet httpGet = new HttpGet(store.getApiUrl());
				response = httpclient.execute(httpGet);
				HttpEntity entity = response.getEntity();

				processStore(entity, store);
				EntityUtils.consume(entity);

			} catch (IOException e) {
				this.getWarnings().add(store.getName());
				this.updateMessage("Error accessing store " + store.getName() + ". Check your network connectivity");
				log.error("Error accessing store " + store.getName() + ". Check your network connectivity", e);

			} catch (StoreParsingException e) {
				this.getWarnings().add(store.getName());
				this.updateMessage("Error parsing store response");
				log.error("Error parsing store response", e);
				
			} finally {
				this.commitProgress(1);
				try {
					if (response != null) {
						response.close();
					}
				} catch (IOException e) {
					log.error("Error closing response", e);
				}
			}
		}

		this.commitProgress(1);
		
		if (this.getWarnings().isEmpty()) {
			this.updateMessage("Plugin stores synced.");

		} else {
			this.updateMessage("Plugin stores synced. Error accessing stores " + String.join(",", this.getWarnings()));
		}

		return success();
	}

	private void processStore(HttpEntity entity, Store store) throws StoreParsingException {

		ObjectMapper objectMapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			StoreJsonMapper pluginStoreTO = objectMapper
					.readValue(entity.getContent(), StoreJsonMapper.class);
			for (ProductJsonMapper productTO : pluginStoreTO.getProducts()) {
				StoreProduct product = StoreModelAdapter.jsonMapperToEntity(productTO);
				product.setStore(store);
				storeProductDAO.save(product);
			}
		} catch (Exception e) {
			throw new StoreParsingException(e);
		}
	}
	
	private class StoreParsingException extends Exception {
		StoreParsingException(Exception e) { 
			super(e); 
		}
	}
}
