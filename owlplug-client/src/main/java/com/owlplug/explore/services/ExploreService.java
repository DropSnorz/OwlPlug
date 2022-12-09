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

package com.owlplug.explore.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.platform.RuntimePlatform;
import com.owlplug.core.services.BaseService;
import com.owlplug.explore.components.ExploreTaskFactory;
import com.owlplug.explore.dao.RemoteSourceDAO;
import com.owlplug.explore.dao.RemotePackageDAO;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.SourceType;
import com.owlplug.explore.model.json.RegistryJsonMapper;
import com.owlplug.explore.model.json.RegistryModelAdapter;
import com.owlplug.explore.model.json.legacy.StoreJsonMapper;
import com.owlplug.explore.model.json.legacy.StoreModelAdapter;
import com.owlplug.explore.model.search.StoreCriteriaAdapter;
import com.owlplug.explore.model.search.StoreFilterCriteria;
import java.io.IOException;
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
public class ExploreService extends BaseService {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private RemoteSourceDAO remoteSourceDAO;
  @Autowired
  private RemotePackageDAO remotePackageDAO;
  @Autowired
  private ExploreTaskFactory exploreTaskFactory;

  @PostConstruct
  private void init() {

    RemoteSource remoteSource = remoteSourceDAO.findByName("OwlPlug Central");

    if (remoteSource == null) {
      remoteSource = new RemoteSource();
      remoteSource.setName("OwlPlug Central");
    }
    remoteSource.setApiUrl("https://central.owlplug.com/store.json");
    remoteSource.setUrl("https://central.owlplug.com");
    remoteSource.setType(SourceType.OWLPLUG_STORE);

    remoteSourceDAO.save(remoteSource);
  }

  /**
   * Triggers Store sync task
   */
  public void syncSources() {
    exploreTaskFactory.createSourceSyncTask().schedule();
  }

  public Iterable<RemoteSource> getRemoteSources() {
    return remoteSourceDAO.findAll();
  }

  /**
   * Retrieves products from store with name matching the given criteria and
   * compatible with the current platform.
   *
   * @param criteriaList criteria list
   * @return list of store products
   */
  public Iterable<RemotePackage> getRemotePackages(List<StoreFilterCriteria> criteriaList) {
    RuntimePlatform env = this.getApplicationDefaults().getRuntimePlatform();

    Specification<RemotePackage> spec = RemotePackageDAO.sourceEnabled()
      .and(RemotePackageDAO.hasPlatformTag(env.getCompatiblePlatformsTags()));
    spec = spec.and(StoreCriteriaAdapter.toSpecification(criteriaList));

    return remotePackageDAO.findAll(spec);
  }

  public Iterable<RemotePackage> getPackagesByName(String name) {
    return remotePackageDAO.findByNameContainingIgnoreCase(name);
  }

  /**
   * Find the best bundle from a product based on the user current platform.
   *
   * @param product - The store product
   */
  public PackageBundle findBestBundle(RemotePackage product) {

    RuntimePlatform runtimePlatform = this.getApplicationDefaults().getRuntimePlatform();

    // Look for bundles matching runtimePlatform
    for (PackageBundle bundle : product.getBundles()) {
      for (String platform : bundle.getTargets()) {
        if (platform.equals(runtimePlatform.getTag())
          || platform.equals(runtimePlatform.getOperatingSystem().getCode())) {
          return bundle;
        }
      }
    }

    // Look for bundles compatibles with current runtimePlatform
    for (PackageBundle bundle : product.getBundles()) {
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

  public RemoteSource getSourceFromRemoteUrl(String url) {

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

      HttpGet httpGet = new HttpGet(url);
      CloseableHttpResponse response = httpclient.execute(httpGet);
      HttpEntity entity = response.getEntity();
      String responseContent = new String(entity.getContent().readAllBytes(), StandardCharsets.UTF_8);
      RemoteSource remoteSource = getSourceFromStoreSpec(responseContent);

      if(remoteSource != null) {
        EntityUtils.consume(entity);
        response.close();
        remoteSource.setApiUrl(url);
        remoteSource.setType(SourceType.OWLPLUG_STORE);
        return remoteSource;
      }

      remoteSource = getSourceFromRegistrySpec(responseContent);

      if(remoteSource != null) {
        EntityUtils.consume(entity);
        response.close();
        remoteSource.setApiUrl(url);
        remoteSource.setType(SourceType.OWLPLUG_REGISTRY);
        return remoteSource;
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
  private RemoteSource getSourceFromStoreSpec(String content) {

    try {
      ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      StoreJsonMapper pluginStoreTO = objectMapper.readValue(content, StoreJsonMapper.class);

      if(pluginStoreTO.getProducts() != null) {
        RemoteSource remoteSource = StoreModelAdapter.jsonMapperToEntity(pluginStoreTO);
        return remoteSource;
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
  private RemoteSource getSourceFromRegistrySpec(String content) {

    try {
      ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      RegistryJsonMapper registryMapper = objectMapper.readValue(content, RegistryJsonMapper.class);
      RemoteSource remoteSource = RegistryModelAdapter.jsonMapperToEntity(registryMapper);
      return remoteSource;
    } catch (JsonProcessingException e) {
      log.debug("Content don't match the Store spec", e);
      return null;
    }
  }

  public void enableSource(RemoteSource remoteSource, boolean enabled) {
    remoteSource.setEnabled(enabled);
    remoteSourceDAO.save(remoteSource);
  }

  public RemoteSource save(RemoteSource remoteSource) {
    return remoteSourceDAO.save(remoteSource);
  }

  public void delete(RemoteSource remoteSource) {
    remoteSourceDAO.delete(remoteSource);
  }

  /**
   * Returns the bundle installation folder based on the plugin type.
   *
   * @param bundle - bundle to install
   * @return path to install directory
   */
  public String getBundleInstallFolder(PackageBundle bundle) {

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
    return remotePackageDAO.findDistinctCreators();
  }
}
