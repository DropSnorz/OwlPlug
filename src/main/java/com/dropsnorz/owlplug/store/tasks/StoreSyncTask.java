package com.dropsnorz.owlplug.store.tasks;

import com.dropsnorz.owlplug.core.engine.tasks.AbstractTask;
import com.dropsnorz.owlplug.core.engine.tasks.TaskException;
import com.dropsnorz.owlplug.core.engine.tasks.TaskResult;
import com.dropsnorz.owlplug.store.dao.PluginStoreDAO;
import com.dropsnorz.owlplug.store.dao.StoreProductDAO;
import com.dropsnorz.owlplug.store.model.PluginStore;
import com.dropsnorz.owlplug.store.model.StaticStoreProduct;
import com.dropsnorz.owlplug.store.model.json.PluginStoreTO;
import com.dropsnorz.owlplug.store.model.json.ProductTO;
import com.dropsnorz.owlplug.store.model.json.StoreModelConverter;
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

	private PluginStoreDAO pluginStoreDAO;
	private StoreProductDAO storeProductDAO;
	
	
	public StoreSyncTask(PluginStoreDAO pluginStoreDAO, StoreProductDAO storeProductDAO) {
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

			for (PluginStore store : pluginStoreDAO.findAll()) {
				log.debug("Exploring store {}", store.getName());
				HttpGet httpGet = new HttpGet(store.getUrl());
				CloseableHttpResponse response = httpclient.execute(httpGet);

				HttpEntity entity = response.getEntity();
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					PluginStoreTO pluginStoreTO = objectMapper.readValue(entity.getContent(), PluginStoreTO.class);
					log.debug(pluginStoreTO.toString());
					for (ProductTO productTO : pluginStoreTO.getProducts()) {
						StaticStoreProduct product = StoreModelConverter.fromTO(productTO);
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
