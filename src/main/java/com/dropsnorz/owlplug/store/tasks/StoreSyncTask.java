package com.dropsnorz.owlplug.store.tasks;

import com.dropsnorz.owlplug.core.tasks.AbstractTask;
import com.dropsnorz.owlplug.core.tasks.TaskException;
import com.dropsnorz.owlplug.core.tasks.TaskResult;
import com.dropsnorz.owlplug.store.dao.StoreDAO;
import com.dropsnorz.owlplug.store.dao.StoreProductDAO;
import com.dropsnorz.owlplug.store.model.Store;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.dropsnorz.owlplug.store.model.json.StoreJsonMapper;
import com.dropsnorz.owlplug.store.model.json.ProductJsonMapper;
import com.dropsnorz.owlplug.store.model.json.StoreModelAdapter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
		super();
		this.pluginStoreDAO = pluginStoreDAO;
		this.storeProductDAO = storeProductDAO;
		setName("Sync plugin stores");
	}


	@Override
	protected TaskResult call() throws Exception {
		
		this.updateProgress(0, 2);
		this.updateMessage("Sync plugins stores");

		storeProductDAO.deleteAll();
		
		this.updateProgress(1, 2);

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

			for (Store store : pluginStoreDAO.findAll()) {
				log.debug("Exploring store {}", store.getName());
				HttpGet httpGet = new HttpGet(store.getUrl());
				CloseableHttpResponse response = httpclient.execute(httpGet);

				HttpEntity entity = response.getEntity();
				ObjectMapper objectMapper = new ObjectMapper()
						.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				try {
					StoreJsonMapper pluginStoreTO = objectMapper
							.readValue(entity.getContent(), StoreJsonMapper.class);
					log.debug(pluginStoreTO.toString());
					for (ProductJsonMapper productTO : pluginStoreTO.getProducts()) {
						StoreProduct product = StoreModelAdapter.jsonMapperToEntity(productTO);
						product.setStore(store);
						storeProductDAO.save(product);
					}
					EntityUtils.consume(entity);
				} catch (Exception e) {
					log.error("Error parsing store response", e);
					throw new TaskException(e);
				} finally {
					response.close();
				}
			}
			this.updateProgress(2, 2);
			this.updateMessage("Plugin stores synced.");

		} catch (Exception e) {
			this.updateProgress(1, 2);
			this.updateMessage("Error accessing store. Check your network connectivity");
			log.error("Error accessing store. Check your network connectivity");
			throw new TaskException(e);

		}
		return success();
	}
}
