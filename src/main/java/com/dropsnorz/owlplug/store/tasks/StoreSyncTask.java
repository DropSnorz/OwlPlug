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

		this.updateProgress(0, 2);
		this.updateMessage("Sync plugins stores");

		storeProductDAO.deleteAll();

		this.updateProgress(1, 2);
		CloseableHttpResponse response = null;

		for (Store store : pluginStoreDAO.findAll()) {
			try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
				log.debug("Exploring store {}", store.getName());
				HttpGet httpGet = new HttpGet(store.getApiUrl());
				response = httpclient.execute(httpGet);
				HttpEntity entity = response.getEntity();

				processStore(entity, store);
				EntityUtils.consume(entity);

			} catch (IOException e) {
				this.updateProgress(1, 2);
				this.updateMessage("Error accessing store. Check your network connectivity");
				log.error("Error accessing store. Check your network connectivity", e);
				throw new TaskException(e);

			} finally {
				try {
					if (response != null) {
						response.close();
					}
				} catch (IOException e) {
					log.error("Error closing response", e);
				}
			}
		}

		this.updateProgress(2, 2);
		this.updateMessage("Plugin stores synced.");

		return success();
	}

	private void processStore(HttpEntity entity, Store store) throws TaskException {

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
			log.error("Error parsing store response", e);
			this.updateMessage("Error parsing store response");
			throw new TaskException(e);
		}
	}
}
