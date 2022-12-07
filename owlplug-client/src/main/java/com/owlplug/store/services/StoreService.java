/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.owlplug.store.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.platform.RuntimePlatform;
import com.owlplug.core.services.BaseService;
import com.owlplug.store.components.StoreTaskFactory;
import com.owlplug.store.dao.StoreDAO;
import com.owlplug.store.dao.StoreProductDAO;
import com.owlplug.store.model.ProductBundle;
import com.owlplug.store.model.Store;
import com.owlplug.store.model.StoreProduct;
import com.owlplug.store.model.StoreType;
import com.owlplug.store.model.json.RegistryJsonMapper;
import com.owlplug.store.model.json.RegistryModelAdapter;
import com.owlplug.store.model.json.legacy.StoreJsonMapper;
import com.owlplug.store.model.json.legacy.StoreModelAdapter;
import com.owlplug.store.model.search.StoreCriteriaAdapter;
import com.owlplug.store.model.search.StoreFilterCriteria;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class StoreService extends BaseService {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private StoreDAO storeDAO;
  @Autowired
  private StoreProductDAO storeProductDAO;
  @Autowired
  private StoreTaskFactory storeTaskFactory;

  @PostConstruct
  private void init() {

    Store store = storeDAO.findByName("OwlPlug Central");

    if (store == null) {
      store = new Store();
      store.setName("OwlPlug Central");
    }
    store.setApiUrl("https://central.owlplug.com/store.json");
    store.setUrl("https://central.owlplug.com");
    store.setType(StoreType.OWLPLUG_STORE);

    storeDAO.save(store);
  }

  /**
   * Triggers Store sync task
   */
  public void syncStores() {
    storeTaskFactory.createStoreSyncTask().schedule();
  }

  public Iterable<Store> getStores() {
    return storeDAO.findAll();
  }

  /**
   * Retrieves products from store with name matching the given criteria and
   * compatible with the current platform.
   *
   * @param criteriaList criteria list
   * @return list of store products
   */
  public Iterable<StoreProduct> getStoreProducts(List<StoreFilterCriteria> criteriaList) {
    RuntimePlatform env = this.getApplicationDefaults().getRuntimePlatform();

    Specification<StoreProduct> spec = StoreProductDAO.storeEnabled()
      .and(StoreProductDAO.hasPlatformTag(env.getCompatiblePlatformsTags()));
    spec = spec.and(StoreCriteriaAdapter.toSpecification(criteriaList));

    return storeProductDAO.findAll(spec);
  }

  public Iterable<StoreProduct> getProductsByName(String name) {
    return storeProductDAO.findByNameContainingIgnoreCase(name);
  }

  /**
   * Find the best bundle from a product based on the user current platform.
   *
   * @param product - The store product
   */
  public ProductBundle findBestBundle(StoreProduct product) {

    RuntimePlatform runtimePlatform = this.getApplicationDefaults().getRuntimePlatform();

    // Look for bundles matching runtimePlatform
    for (ProductBundle bundle : product.getBundles()) {
      for (String platform : bundle.getTargets()) {
        if (platform.equals(runtimePlatform.getTag())
          || platform.equals(runtimePlatform.getOperatingSystem().getCode())) {
          return bundle;
        }
      }
    }

    // Look for bundles compatibles with current runtimePlatform
    for (ProductBundle bundle : product.getBundles()) {
      for (String platformTag : bundle.getTargets()) {
        for (String compatibleTag : runtimePlatform.getCompatiblePlatformsTags()) {
          if (platformTag.equals(compatibleTag)) {
            return bundle;
          }
        }
      }
    }
    return null;
  }

  public Store getStoreFromRemoteUrl(String url) {

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

      HttpGet httpGet = new HttpGet(url);
      CloseableHttpResponse response = httpclient.execute(httpGet);
      HttpEntity entity = response.getEntity();
      String responseContent = new String(entity.getContent().readAllBytes(), StandardCharsets.UTF_8);
      Store store = getStoreFromStoreSpec(responseContent);

      if(store != null) {
        EntityUtils.consume(entity);
        response.close();
        store.setApiUrl(url);
        store.setType(StoreType.OWLPLUG_STORE);
        return store;
      }

      store = getStoreFromRegistrySpec(responseContent);

      if(store != null) {
        EntityUtils.consume(entity);
        response.close();
        store.setApiUrl(url);
        store.setType(StoreType.OWLPLUG_REGISTRY);
        return store;
      }

      return null;

    } catch (IOException e) {
      log.error("Error accessing store: Check your network connectivity", e);
      return null;
    }

  }

  /**
   * Creates a PluginStore instance requesting a store url endpoint.
   *
   * @param content Store content
   * @return created pluginstore instance, null if an error occurs
   */
  private Store getStoreFromStoreSpec(String content) {

    try {
      ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      StoreJsonMapper pluginStoreTO = objectMapper.readValue(content, StoreJsonMapper.class);

      if(pluginStoreTO.getProducts() != null) {
        Store store = StoreModelAdapter.jsonMapperToEntity(pluginStoreTO);
        return store;
      } else {
        log.debug("No products defined in store");
        return null;
      }

    } catch (JsonProcessingException e) {
      log.debug("Content don't match the Store spec", e);
      return null;
    }
  }

  /**
   * Creates a PluginStore instance requesting a store url endpoint.
   *
   * @param content Store content
   * @return created pluginstore instance, null if an error occurs
   */
  private Store getStoreFromRegistrySpec(String content) {

    try {
      ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      RegistryJsonMapper registryMapper = objectMapper.readValue(content, RegistryJsonMapper.class);
      Store store = RegistryModelAdapter.jsonMapperToEntity(registryMapper);
      return store;
    } catch (JsonProcessingException e) {
      log.debug("Content don't match the Store spec", e);
      return null;
    }
  }

  public void enableStore(Store store, boolean enabled) {
    store.setEnabled(enabled);
    storeDAO.save(store);
  }

  public Store save(Store store) {
    return storeDAO.save(store);
  }

  public void delete(Store store) {
    storeDAO.delete(store);
  }

  /**
   * Returns the bundle installation folder based on the plugin type.
   *
   * @param bundle - bundle to install
   * @return path to install directory
   */
  public String getBundleInstallFolder(ProductBundle bundle) {

    if (bundle.getFormat().equals(PluginFormat.VST2)) {
      return this.getPreferences().get(ApplicationDefaults.VST_DIRECTORY_KEY, "");
    } else if (bundle.getFormat().equals(PluginFormat.VST3)) {
      return this.getPreferences().get(ApplicationDefaults.VST3_DIRECTORY_KEY, "");
    } else if (bundle.getFormat().equals(PluginFormat.AU)) {
      return this.getPreferences().get(ApplicationDefaults.AU_DIRECTORY_KEY, "");
    } else if (bundle.getFormat().equals(PluginFormat.LV2)) {
      return this.getPreferences().get(ApplicationDefaults.LV2_DIRECTORY_KEY, "");
    }

    return this.getPreferences().get(ApplicationDefaults.VST_DIRECTORY_KEY, "");

  }

  /**
   * Returns all distinct product creators.
   *
   * @return lit of creators
   */
  public List<String> getDistinctCreators() {
    return storeProductDAO.findDistinctCreators();
  }
}
