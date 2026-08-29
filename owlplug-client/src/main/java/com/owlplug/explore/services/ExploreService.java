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
import com.owlplug.core.model.RuntimePlatform;
import com.owlplug.core.services.BaseService;
import com.owlplug.explore.events.RemoteSourceUpdatedEvent;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.model.SourceType;
import com.owlplug.explore.model.mappers.oas.OASFile;
import com.owlplug.explore.model.mappers.oas.OASModelAdapter;
import com.owlplug.explore.model.mappers.oas.OASPlugin;
import com.owlplug.explore.model.mappers.oas.OASRegistry;
import com.owlplug.explore.model.mappers.registry.RegistryMapper;
import com.owlplug.explore.model.mappers.registry.RegistryModelAdapter;
import com.owlplug.explore.model.search.ExploreCriteriaAdapter;
import com.owlplug.explore.model.search.ExploreFilterCriteria;
import com.owlplug.explore.repositories.RemotePackageRepository;
import com.owlplug.explore.repositories.RemoteSourceRepository;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.services.PluginService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ExploreService extends BaseService {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ApplicationEventPublisher publisher;
  @Autowired
  private RemoteSourceRepository remoteSourceRepository;
  @Autowired
  private RemotePackageRepository remotePackageRepository;
  @Autowired
  @Lazy
  private PluginService pluginService;

  @PostConstruct
  private void init() {

    RemoteSource owlplugRegistry = remoteSourceRepository.findByUrl(this.getApplicationDefaults().getOwlPlugRegistryUrl());

    if (owlplugRegistry == null) {
      owlplugRegistry = new RemoteSource();
      owlplugRegistry.setName("OwlPlug Registry");
    }
    owlplugRegistry.setUrl(this.getApplicationDefaults().getOwlPlugRegistryUrl());
    owlplugRegistry.setDisplayUrl("https://registry.owlplug.com");
    owlplugRegistry.setType(SourceType.OWLPLUG_REGISTRY);

    remoteSourceRepository.save(owlplugRegistry);

    RemoteSource oasRegistry = remoteSourceRepository.findByUrl(this.getApplicationDefaults().getOpenAudioRegistryUrl());

    if (oasRegistry == null) {
      oasRegistry = new RemoteSource();
      oasRegistry.setName("Open Audio Registry");
    }
    oasRegistry.setUrl(this.getApplicationDefaults().getOpenAudioRegistryUrl());
    oasRegistry.setDisplayUrl("https://github.com/open-audio-stack");
    oasRegistry.setType(SourceType.OAS_REGISTRY);

    remoteSourceRepository.save(oasRegistry);
  }

  public Iterable<RemoteSource> getRemoteSources() {
    return remoteSourceRepository.findAll();
  }

  public RemoteSource getRemoteSourceByUrl(String url) {
    return remoteSourceRepository.findByUrl(url);
  }

  /**
   * Retrieves a page of packages from store with name matching the given criteria and
   * compatible with the current platform.
   *
   * <p>Pagination is applied on package ids first (without the bundles/targets fetch join), then
   * the matching page of entities is hydrated with its collections in a second query. Applying a
   * {@link Pageable} directly on top of a to-many fetch join is not supported by Hibernate: it
   * would fall back to fetching every matching row and paginating in memory.
   *
   * @param criteriaList criteria list
   * @param pageable page request
   * @return page of remote packages
   */
  public Page<RemotePackage> getRemotePackages(List<ExploreFilterCriteria> criteriaList, Pageable pageable) {
    Specification<RemotePackage> filterSpec = RemotePackageRepository.sourceEnabled()
        .and(RemotePackageRepository.hasBundles())
        .and(ExploreCriteriaAdapter.toSpecification(criteriaList));

    // Append id as a stable tie-breaker so rows with equal sort keys keep a deterministic order
    // across pages; without it, LIMIT/OFFSET pagination can skip or duplicate rows between calls.
    Sort deterministicSort = pageable.getSort().and(Sort.by(Sort.Direction.ASC, "id"));
    Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), deterministicSort);

    Page<RemotePackage> idPage = remotePackageRepository.findAll(filterSpec, sortedPageable);
    List<Long> ids = idPage.getContent().stream().map(RemotePackage::getId).toList();

    if (ids.isEmpty()) {
      return new PageImpl<>(List.of(), sortedPageable, idPage.getTotalElements());
    }

    // findAll(fetchSpec) is not guaranteed to return rows in the same order as ids (the paginated
    // order from idPage), so this map is keyed by id purely to look entities up and rebuild that
    // order below - it is not a dedup mechanism. fetchBundlesAndTargets() sets distinct(true), so
    // the fetch join is not expected to yield more than one row per id; toMap is left merge-free
    // (throwing on a duplicate key) so a regression there fails loudly instead of being masked.
    Specification<RemotePackage> fetchSpec = RemotePackageRepository.hasIdIn(ids)
        .and(RemotePackageRepository.fetchBundlesAndTargets());
    Map<Long, RemotePackage> fetchedById = remotePackageRepository.findAll(fetchSpec).stream()
        .collect(Collectors.toMap(RemotePackage::getId, Function.identity()));

    // A package can be absent here if it was deleted between the id query above and this one
    // (e.g. a concurrent source sync); skip it rather than returning a null page entry.
    List<RemotePackage> orderedPage = ids.stream()
        .map(fetchedById::get)
        .filter(remotePackage -> {
          if (remotePackage == null) {
            log.debug("Skipping package id no longer present during Explore page hydration");
          }
          return remotePackage != null;
        })
        .toList();

    return new PageImpl<>(orderedPage, sortedPageable, idPage.getTotalElements());
  }

  public Iterable<RemotePackage> getPackagesByName(String name) {
    return remotePackageRepository.findByNameContainingIgnoreCase(name);
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
   * Fetches a single file's download details from the OAS registry detail endpoint
   * (GET {registryUrl}/plugins/{slug}/{version}), using the bundle's order as the
   * positional index into the returned files array.
   *
   * @param remoteSource the OAS_REGISTRY remote source the package belongs to
   * @param remotePackage the package the bundle belongs to
   * @param bundle the bundle whose file details need to be resolved
   * @return the matching OASFile, or null if it can't be resolved
   */
  public OASFile fetchOASFile(RemoteSource remoteSource, RemotePackage remotePackage, PackageBundle bundle) {

    String url = remoteSource.getUrl() + "/plugins/" + remotePackage.getSlug() + "/" + remotePackage.getVersion();
    HttpGet httpGet = new HttpGet(url);

    try (CloseableHttpClient httpclient = HttpClients.createDefault();
         CloseableHttpResponse response = httpclient.execute(httpGet)) {

      HttpEntity entity = response.getEntity();
      String content = new String(entity.getContent().readAllBytes(), StandardCharsets.UTF_8);

      ObjectMapper objectMapper = new ObjectMapper().configure(
          DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      OASPlugin plugin = objectMapper.readValue(content, OASPlugin.class);

      if (plugin.getFiles() == null) {
        return null;
      }

      int index = bundle.getOrder();
      if (index < 0 || index >= plugin.getFiles().size()) {
        return null;
      }

      return plugin.getFiles().get(index);

    } catch (IOException e) {
      log.error("Error fetching OAS file details for {} {}", remotePackage.getSlug(), remotePackage.getVersion(), e);
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
      return RegistryModelAdapter.jsonMapperToEntity(registryMapper);
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

      return OASModelAdapter.mapperToEntity(registryMapper);
    } catch (JsonProcessingException e) {
      log.debug("Content don't match the Store spec", e);
      return null;
    }
  }


  public void enableSource(RemoteSource remoteSource, boolean enabled) {
    remoteSource.setEnabled(enabled);
    remoteSourceRepository.save(remoteSource);
    publisher.publishEvent(new RemoteSourceUpdatedEvent(remoteSource));
  }

  public RemoteSource save(RemoteSource remoteSource) {
    RemoteSource saved = remoteSourceRepository.save(remoteSource);
    publisher.publishEvent(new RemoteSourceUpdatedEvent(saved));
    return saved;
  }

  public void delete(RemoteSource remoteSource) {
    remoteSourceRepository.delete(remoteSource);
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
    return remotePackageRepository.findDistinctCreators();
  }
}
