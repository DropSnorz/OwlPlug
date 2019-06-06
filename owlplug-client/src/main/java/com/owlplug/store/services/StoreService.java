/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.platform.RuntimePlatform;
import com.owlplug.store.dao.StoreDAO;
import com.owlplug.store.dao.StoreProductDAO;
import com.owlplug.store.model.ProductBundle;
import com.owlplug.store.model.Store;
import com.owlplug.store.model.StoreProduct;
import com.owlplug.store.model.json.StoreJsonMapper;
import com.owlplug.store.model.json.StoreModelAdapter;
import com.owlplug.store.model.search.StoreCriteriaAdapter;
import com.owlplug.store.model.search.StoreFilterCriteria;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;
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
public class StoreService {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ApplicationDefaults applicationDefaults;
  @Autowired
  private Preferences prefs;
  @Autowired
  private StoreDAO storeDAO;
  @Autowired
  private StoreProductDAO storeProductDAO;

  @PostConstruct
  private void init() {

    Store store = storeDAO.findByName("OwlPlug Central");

    if (store == null) {
      store = new Store();
      store.setName("OwlPlug Central");
    }
    store.setApiUrl("https://central.owlplug.com/store.json");
    store.setUrl("https://central.owlplug.com");

    storeDAO.save(store);
  }
  
  public Iterable<Store> getStores() {
    return storeDAO.findAll();
  }

  /**
   * Retrieves products from store with name matching the given criterias and
   * compatible with the current platform.
   * 
   * @param criteriaList criteria list
   * @return list of store products
   */
  public Iterable<StoreProduct> getStoreProducts(List<StoreFilterCriteria> criteriaList) {
    RuntimePlatform env = applicationDefaults.getRuntimePlatform();

    Specification<StoreProduct> spec = StoreProductDAO.storeEnabled()
        .and(StoreProductDAO.hasPlatformTag(env.getCompatiblePlatformsTags()));
    spec = spec.and(StoreCriteriaAdapter.toSpecification(criteriaList));

    return storeProductDAO.findAll(spec);
  }

  public Iterable<StoreProduct> getProductsByName(String name) {
    return storeProductDAO.findByNameContainingIgnoreCase(name);
  }

  /**
   * Find the best bundle from a prpduct based on the user current platform.
   * 
   * @param product - The store product
   */
  public ProductBundle findBestBundle(StoreProduct product) {

    RuntimePlatform runtimePlatform = applicationDefaults.getRuntimePlatform();

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

  /**
   * Creates a PluginStore instance requesting a store url endpoint.
   * 
   * @param url Store endpoint url
   * @return created pluginstore instance, null if an error occurs
   */
  public Store getPluginStoreFromUrl(String url) {

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

      HttpGet httpGet = new HttpGet(url);
      CloseableHttpResponse response = httpclient.execute(httpGet);

      HttpEntity entity = response.getEntity();
      ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
          false);
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
   * Returns the bundle installation folder based on the plugin type
   * @param bundle - bundle to install
   * @return path to install directory
   */
  public String getBundleInstallFolder(ProductBundle bundle) {
       
    if (bundle.getFormat().equals(PluginFormat.VST2)) {
      return prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, "");
    }
    
    if (bundle.getFormat().equals(PluginFormat.VST3)) {
      return prefs.get(ApplicationDefaults.VST3_DIRECTORY_KEY, "");
    }
    
    return prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, "");
        
  }
}
