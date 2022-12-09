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
 
package com.owlplug.store.tasks;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskException;
import com.owlplug.core.tasks.TaskResult;
import com.owlplug.store.dao.StoreDAO;
import com.owlplug.store.dao.StoreProductDAO;
import com.owlplug.store.model.Store;
import com.owlplug.store.model.StoreProduct;
import com.owlplug.store.model.StoreType;
import com.owlplug.store.model.json.PackageJsonMapper;
import com.owlplug.store.model.json.PackageVersionJsonMapper;
import com.owlplug.store.model.json.RegistryJsonMapper;
import com.owlplug.store.model.json.RegistryModelAdapter;
import com.owlplug.store.model.json.legacy.ProductJsonMapper;
import com.owlplug.store.model.json.legacy.StoreJsonMapper;
import com.owlplug.store.model.json.legacy.StoreModelAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
   * 
   * @param pluginStoreDAO  pluginStore DAO
   * @param storeProductDAO storeProduct DAO
   */
  public StoreSyncTask(StoreDAO pluginStoreDAO, StoreProductDAO storeProductDAO) {
    super("Sync plugin stores");
    this.pluginStoreDAO = pluginStoreDAO;
    this.storeProductDAO = storeProductDAO;
  }

  @Override
  protected TaskResult call() throws TaskException {

    this.updateMessage("Sync plugins stores");
    this.commitProgress(-1);

    Iterable<Store> storeList = pluginStoreDAO.findAll();
    this.setMaxProgress(2 + Iterables.size(storeList));

    storeProductDAO.deleteAll();

    this.commitProgress(2);
    CloseableHttpResponse response = null;

    for (Store store : pluginStoreDAO.findAll()) {
      try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
        log.debug("Exploring store {}", store.getName());
        this.updateMessage("Exploring store " + store.getName());
        HttpGet httpGet = new HttpGet(store.getApiUrl());
        response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if (store.getType() == null || store.getType().equals(StoreType.OWLPLUG_STORE)) {
          processStore(entity, store);
        } else if (store.getType().equals(StoreType.OWLPLUG_REGISTRY)) {
          processRegistry(entity, store);
        }

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

    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        false);

    try {
      StoreJsonMapper pluginStoreTO = objectMapper.readValue(entity.getContent(), StoreJsonMapper.class);
      List<List<ProductJsonMapper>> chunks = Lists.partition(pluginStoreTO.getProducts(), 100);
      
      for (List<ProductJsonMapper> partition : chunks) {
        List<StoreProduct> storeProductPartition = new ArrayList<>();
        for (ProductJsonMapper productMapper : partition) {
          StoreProduct product = StoreModelAdapter.jsonMapperToEntity(productMapper);
          product.setStore(store);
          storeProductPartition.add(product);
        }
        storeProductDAO.saveAll(storeProductPartition);
      }
      
    } catch (Exception e) {
      throw new StoreParsingException(e);
    }
  }

  private void processRegistry(HttpEntity entity, Store store) throws StoreParsingException {
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        false);

    try {
      RegistryJsonMapper registryMapper = objectMapper.readValue(entity.getContent(), RegistryJsonMapper.class);
      List<PackageJsonMapper> packages = new ArrayList(registryMapper.getPackages().values());
      List<List<PackageJsonMapper>> chunks = Lists.partition(packages, 100);

      for (List<PackageJsonMapper> partition : chunks) {
        List<StoreProduct> storeProductPartition = new ArrayList<>();
        for (PackageJsonMapper packageMapper : partition) {

          if (packageMapper.getVersions().containsKey(packageMapper.getLatestVersion())) {

            PackageVersionJsonMapper latestPackage = packageMapper.getVersions()
              .get(packageMapper.getLatestVersion());

            StoreProduct product = RegistryModelAdapter.jsonMapperToEntity(latestPackage);
            product.setSlug(packageMapper.getSlug());
            product.setStore(store);
            storeProductPartition.add(product);
          }
        }
        storeProductDAO.saveAll(storeProductPartition);
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
