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
 
package com.owlplug.explore.tasks;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskException;
import com.owlplug.core.tasks.TaskResult;
import com.owlplug.explore.dao.RemoteSourceDAO;
import com.owlplug.explore.dao.RemotePackageDAO;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.SourceType;
import com.owlplug.explore.model.json.PackageJsonMapper;
import com.owlplug.explore.model.json.PackageVersionJsonMapper;
import com.owlplug.explore.model.json.RegistryJsonMapper;
import com.owlplug.explore.model.json.RegistryModelAdapter;
import com.owlplug.explore.model.json.legacy.ProductJsonMapper;
import com.owlplug.explore.model.json.legacy.StoreJsonMapper;
import com.owlplug.explore.model.json.legacy.StoreModelAdapter;
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

public class SourceSyncTask extends AbstractTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private RemoteSourceDAO remoteSourceDAO;
  private RemotePackageDAO remotePackageDAO;

  /**
   * Creates a new SourceSync tasks.
   * 
   * @param remoteSourceDAO  remoteSource DAO
   * @param remotePackageDAO remotePackage DAO
   */
  public SourceSyncTask(RemoteSourceDAO remoteSourceDAO, RemotePackageDAO remotePackageDAO) {
    super("Syncing plugin sources");
    this.remoteSourceDAO = remoteSourceDAO;
    this.remotePackageDAO = remotePackageDAO;
  }

  @Override
  protected TaskResult call() throws TaskException {

    this.updateMessage("Syncing plugins stores");
    this.commitProgress(-1);

    Iterable<RemoteSource> storeList = remoteSourceDAO.findAll();
    this.setMaxProgress(2 + Iterables.size(storeList));

    remotePackageDAO.deleteAll();

    this.commitProgress(2);
    CloseableHttpResponse response = null;

    for (RemoteSource remoteSource : remoteSourceDAO.findAll()) {
      try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
        log.debug("Exploring source {} - {}", remoteSource.getName(), remoteSource.getType().getLabel());
        this.updateMessage("Exploring source " + remoteSource.getName() +
          " - " + remoteSource.getType().getLabel());
        HttpGet httpGet = new HttpGet(remoteSource.getApiUrl());
        response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if (remoteSource.getType() == null || remoteSource.getType().equals(SourceType.OWLPLUG_STORE)) {
          processStoreSource(entity, remoteSource);
        } else if (remoteSource.getType().equals(SourceType.OWLPLUG_REGISTRY)) {
          processRegistrySource(entity, remoteSource);
        }

        EntityUtils.consume(entity);

      } catch (IOException e) {
        this.getWarnings().add(remoteSource.getName());
        this.updateMessage("Error accessing source " + remoteSource.getName() + ". Check your network connectivity");
        log.error("Error accessing source " + remoteSource.getName() + ". Check your network connectivity", e);

      } catch (StoreParsingException e) {
        this.getWarnings().add(remoteSource.getName());
        this.updateMessage("Error parsing remote source response");
        log.error("Error parsing remote source response", e);

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
      this.updateMessage("Plugin sources synced.");

    } else {
      this.updateMessage("Plugin sources synced. Error accessing sources " + String.join(",", this.getWarnings()));
    }

    return success();
  }

  private void processStoreSource(HttpEntity entity, RemoteSource remoteSource) throws StoreParsingException {

    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        false);

    try {
      StoreJsonMapper pluginStoreTO = objectMapper.readValue(entity.getContent(), StoreJsonMapper.class);
      List<List<ProductJsonMapper>> chunks = Lists.partition(pluginStoreTO.getProducts(), 100);
      
      for (List<ProductJsonMapper> partition : chunks) {
        List<RemotePackage> remotePackagePartition = new ArrayList<>();
        for (ProductJsonMapper productMapper : partition) {
          RemotePackage product = StoreModelAdapter.jsonMapperToEntity(productMapper);
          product.setRemoteSource(remoteSource);
          remotePackagePartition.add(product);
        }
        remotePackageDAO.saveAll(remotePackagePartition);
      }
      
    } catch (Exception e) {
      throw new StoreParsingException(e);
    }
  }

  private void processRegistrySource(HttpEntity entity, RemoteSource remoteSource) throws StoreParsingException {
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        false);

    try {
      RegistryJsonMapper registryMapper = objectMapper.readValue(entity.getContent(), RegistryJsonMapper.class);
      List<PackageJsonMapper> packages = new ArrayList(registryMapper.getPackages().values());
      List<List<PackageJsonMapper>> chunks = Lists.partition(packages, 100);

      for (List<PackageJsonMapper> partition : chunks) {
        List<RemotePackage> remotePackagePartition = new ArrayList<>();
        for (PackageJsonMapper packageMapper : partition) {

          if (packageMapper.getVersions().containsKey(packageMapper.getLatestVersion())) {

            PackageVersionJsonMapper latestPackage = packageMapper.getVersions()
              .get(packageMapper.getLatestVersion());

            RemotePackage product = RegistryModelAdapter.jsonMapperToEntity(latestPackage);
            product.setSlug(packageMapper.getSlug());
            product.setRemoteSource(remoteSource);
            remotePackagePartition.add(product);
          }
        }
        remotePackageDAO.saveAll(remotePackagePartition);
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
