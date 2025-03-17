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
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.platform.RuntimePlatform;
import com.owlplug.core.services.BaseService;
import com.owlplug.core.services.PluginService;
import com.owlplug.explore.components.ExploreTaskFactory;
import com.owlplug.explore.dao.RemotePackageDAO;
import com.owlplug.explore.dao.RemoteSourceDAO;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.model.SourceType;
import com.owlplug.explore.model.mappers.oas.OASModelAdapter;
import com.owlplug.explore.model.mappers.oas.OASRegistry;
import com.owlplug.explore.model.mappers.registry.RegistryMapper;
import com.owlplug.explore.model.mappers.registry.RegistryModelAdapter;
import com.owlplug.explore.model.search.ExploreCriteriaAdapter;
import com.owlplug.explore.model.search.ExploreFilterCriteria;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
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
  @Autowired
  private PluginService pluginService;

  @PostConstruct
  private void init() {

    RemoteSource remoteSource = remoteSourceDAO.findByUrl(this.getApplicationDefaults().getOwlPlugRegistryUrl());

    if (remoteSource == null) {
      remoteSource = new RemoteSource();
      remoteSource.setName("OwlPlug Registry");
    }
    remoteSource.setUrl(this.getApplicationDefaults().getOwlPlugRegistryUrl());
    remoteSource.setDisplayUrl("https://registry.owlplug.com");
    remoteSource.setType(SourceType.OWLPLUG_REGISTRY);

    remoteSourceDAO.save(remoteSource);
  }

  /**
   * Triggers Store sync task.
   */
  public void syncSources() {
    exploreTaskFactory.createSourceSyncTask().schedule();
  }

  public Iterable<RemoteSource> getRemoteSources() {
    return remoteSourceDAO.findAll();
  }

  public RemoteSource getRemoteSourceByUrl(String url) {
    return remoteSourceDAO.findByUrl(url);
  }

  /**
   * Retrieves packages from store with name matching the given criteria and
   * compatible with the current platform.
   *
   * @param criteriaList criteria list
   * @return list of remote packages
   */
  public Iterable<RemotePackage> getRemotePackages(List<ExploreFilterCriteria> criteriaList) {
    RuntimePlatform env = this.getApplicationDefaults().getRuntimePlatform();

    Specification<RemotePackage> spec = RemotePackageDAO.sourceEnabled()
        .and(RemotePackageDAO.hasPlatformTag(env.getCompatiblePlatformsTags()));
    spec = spec.and(ExploreCriteriaAdapter.toSpecification(criteriaList));

    return remotePackageDAO.findAll(spec);
  }

  public Iterable<RemotePackage> getPackagesByName(String name) {
    return remotePackageDAO.findByNameContainingIgnoreCase(name);
  }

  /**
   * Find the best bundle from a package based on the user current platform.
   *
   * @param remotePackage - The remote package
   */
  public PackageBundle findBestBundle(RemotePackage remotePackage) {

    RuntimePlatform runtimePlatform = this.getApplicationDefaults().getRuntimePlatform();

    // Look for bundles matching runtimePlatform
    for (PackageBundle bundle : remotePackage.getBundles()) {
      for (String platform : bundle.getTargets()) {
        if (platform.equals(runtimePlatform.getTag())
            || platform.equals(runtimePlatform.getOperatingSystem().getCode())) {
          return bundle;
        }
      }
    }

    // Look for bundles compatibles with current runtimePlatform
    for (PackageBundle bundle : remotePackage.getBundles()) {
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

  public RemoteSource fetchSourceFromRemoteUrl(String url) {

    HttpGet httpGet = new HttpGet(url);

    try (CloseableHttpClient httpclient = HttpClients.createDefault();
         CloseableHttpResponse response = httpclient.execute(httpGet)) {

      HttpEntity entity = response.getEntity();
      String responseContent = new String(entity.getContent().readAllBytes(), StandardCharsets.UTF_8);

      RemoteSource remoteSource = getSourceFromRegistrySpec(responseContent);

      if (remoteSource != null) {
        remoteSource.setUrl(url);
        remoteSource.setType(SourceType.OWLPLUG_REGISTRY);
        return remoteSource;
      }

      remoteSource = getSourceFromOASRegistrySpec(responseContent);

      if (remoteSource != null) {
        remoteSource.setUrl(url);
        remoteSource.setType(SourceType.OAS_REGISTRY);
        return remoteSource;
      }

      EntityUtils.consume(entity);
      return null;

    } catch (IOException e) {
      log.error("Error accessing store: Check your network connectivity", e);
      return null;
    }

  }

  /**
   * Creates a RemoteSource instance from a raw OwlPlug registry content.
   *
   * @param content Registry content
   * @return created remote source instance, null if an error occurs
   */
  private RemoteSource getSourceFromRegistrySpec(String content) {

    try {
      ObjectMapper objectMapper = new ObjectMapper().configure(
          DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      RegistryMapper registryMapper = objectMapper.readValue(content, RegistryMapper.class);
      if (registryMapper.getPackages() == null) {
        return null;
      }
      RemoteSource remoteSource = RegistryModelAdapter.jsonMapperToEntity(registryMapper);
      return remoteSource;
    } catch (JsonProcessingException e) {
      log.debug("Content don't match the Store spec", e);
      return null;
    }
  }

  /**
   * Creates a RemoteSource instance from a raw OAS registry content.
   *
   * @param content Registry content
   * @return created remote source instance, null if an error occurs
   */
  private RemoteSource getSourceFromOASRegistrySpec(String content) {

    try {
      ObjectMapper objectMapper = new ObjectMapper().configure(
              DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      OASRegistry registryMapper = objectMapper.readValue(content, OASRegistry.class);

      if (registryMapper.getPlugins() == null) {
        return null;
      }

      RemoteSource remoteSource = OASModelAdapter.mapperToEntity(registryMapper);

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

  public boolean canDeterminateBundleInstallFolder(PackageBundle bundle) {

    List<PluginFormat> formats = filterEnabledFormats(bundle.getFormats());
    if (formats.size() == 1) {
      return true;
    } else if (formats.size() > 1) {
      List<String> paths = new ArrayList<>();
      for (PluginFormat format : formats) {
        paths.add(this.pluginService.getPrimaryPluginPathByFormat(format));
      }
      // check if all path are equals
      return paths.stream().allMatch(s -> s.equals(paths.get(0)));
    }

    return false;
  }

  /**
   * Returns the bundle installation folder based on the plugin format.
   * Multiple formats can be embedded in the same bundle, in this case
   * an arbitrary path will be selected between formats.
   *
   * @param bundle - bundle to install
   * @return path to install directory
   */
  public String getBundleInstallFolder(PackageBundle bundle) {

    PluginFormat format = filterEnabledFormats(bundle.getFormats()).getFirst();
    return pluginService.getPrimaryPluginPathByFormat(format);

  }

  private List<PluginFormat> filterEnabledFormats(List<String> formats) {
    List<PluginFormat> filtered = new ArrayList<>();
    for (String formatVal : formats) {
      PluginFormat format = PluginFormat.fromBundleString(formatVal);
      if (this.pluginService.isFormatEnabled(format)) {
        filtered.add(format);
      }
    }
    return filtered;
  }

  /**
   * Returns all distinct package creators.
   *
   * @return lit of creators
   */
  public List<String> getDistinctCreators() {
    return remotePackageDAO.findDistinctCreators();
  }
}
