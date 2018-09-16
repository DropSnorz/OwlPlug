package com.dropsnorz.owlplug.store.service;

import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.dropsnorz.owlplug.core.tasks.TaskException;
import com.dropsnorz.owlplug.store.dao.PluginStoreDAO;
import com.dropsnorz.owlplug.store.dao.StoreProductDAO;
import com.dropsnorz.owlplug.store.model.PluginStore;
import com.dropsnorz.owlplug.store.model.StaticStoreProduct;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.dropsnorz.owlplug.store.model.json.PluginStoreTO;
import com.dropsnorz.owlplug.store.model.json.StoreModelConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
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
	private TaskFactory taskFactory;
	@Autowired
	private PluginStoreDAO pluginStoreDAO;
	@Autowired
	private StoreProductDAO storeProductDAO;


	@PostConstruct
	private void init() {

		PluginStore store = pluginStoreDAO.findByName("OwlPlug Central");

		if (store == null) {
			store = new PluginStore();
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

	/**
	 * Creates a PluginStore instance requesting a store url endpoint.
	 * @param url Store endpoint url
	 * @return created pluginstore instance, null if an error occurs
	 */
	public PluginStore getPluginStoreFromUrl(String url) {

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(httpGet);

			HttpEntity entity = response.getEntity();
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				PluginStoreTO pluginStoreTO = objectMapper.readValue(entity.getContent(), PluginStoreTO.class);
				EntityUtils.consume(entity);
				return StoreModelConverter.fromTO(pluginStoreTO);

			} catch (Exception e) {
				log.error("Error parsing store response: " + url, e);
				throw new TaskException(e);
			} finally {
				response.close();
			}

		} catch (Exception e) {
			return null;

		}

	}

}
