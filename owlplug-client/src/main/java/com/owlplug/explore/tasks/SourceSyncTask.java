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
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.model.SourceType;
import com.owlplug.explore.model.mappers.oas.OASModelAdapter;
import com.owlplug.explore.model.mappers.oas.OASPackage;
import com.owlplug.explore.model.mappers.oas.OASPlugin;
import com.owlplug.explore.model.mappers.oas.OASRegistry;
import com.owlplug.explore.model.mappers.registry.PackageMapper;
import com.owlplug.explore.model.mappers.registry.PackageVersionMapper;
import com.owlplug.explore.model.mappers.registry.RegistryMapper;
import com.owlplug.explore.model.mappers.registry.RegistryModelAdapter;
import com.owlplug.explore.repositories.RemotePackageRepository;
import com.owlplug.explore.repositories.RemoteSourceRepository;
import java.io.IOException;
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

public class SourceSyncTask extends AbstractTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  // TODO temporary check used to validate the OAS lazy download details fetch at install time.
  // Remove once the lazy fetch has been proven reliable against the official registry.
  private static final String OFFICIAL_OAS_REGISTRY_URL =
      "https://open-audio-stack.github.io/open-audio-stack-registry";

  private RemoteSourceRepository remoteSourceRepository;
  private RemotePackageRepository remotePackageRepository;

  private ArrayList<String> warnings = new ArrayList<>();


  /**
   * Creates a new SourceSync tasks.
   *
   * @param remoteSourceRepository  remoteSource Repository
   * @param remotePackageRepository remotePackage Repository
   */
  public SourceSyncTask(RemoteSourceRepository remoteSourceRepository, RemotePackageRepository remotePackageRepository) {
    super("Syncing plugin sources");
    this.remoteSourceRepository = remoteSourceRepository;
    this.remotePackageRepository = remotePackageRepository;
  }

  @Override
  protected TaskResult start() throws TaskException {

    this.updateMessage("Syncing plugins stores");
    this.commitProgress(-1);

    Iterable<RemoteSource> storeList = remoteSourceRepository.findAll();
    this.setMaxProgress(2 + Iterables.size(storeList));

    remotePackageRepository.deleteAll();
    // Flushing context to the database as next queries will recreate entities
    remotePackageRepository.flush();

    this.commitProgress(2);
    CloseableHttpResponse response = null;

    for (RemoteSource remoteSource : remoteSourceRepository.findAll()) {
      try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
        log.debug("Exploring source {} - {}", remoteSource.getName(), remoteSource.getType().getLabel());
        this.updateMessage("Exploring source " + remoteSource.getName()
                               + " - " + remoteSource.getType().getLabel());
        HttpGet httpGet = new HttpGet(remoteSource.getUrl());
        response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if (remoteSource.getType() == null || remoteSource.getType().equals(SourceType.OWLPLUG_REGISTRY)) {
          processRegistrySource(entity, remoteSource);
        } else if (remoteSource.getType().equals(SourceType.OAS_REGISTRY)) {
          processOASSource(entity, remoteSource);
        }

        EntityUtils.consume(entity);

      } catch (IOException e) {
        this.warnings.add(remoteSource.getName());
        this.updateMessage("Error accessing source " + remoteSource.getName() + ". Check your network connectivity");
        log.error("Error accessing source " + remoteSource.getName() + ". Check your network connectivity", e);

      } catch (StoreParsingException e) {
        this.warnings.add(remoteSource.getName());
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

    if (this.warnings.isEmpty()) {
      this.updateMessage("Plugin sources synced.");

    } else {
      this.updateMessage("Plugin sources synced. Error accessing sources " + String.join(",", this.warnings));
    }

    return completed();
  }

  private void processRegistrySource(HttpEntity entity, RemoteSource remoteSource) throws StoreParsingException {
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        false);

    try {
      RegistryMapper registryMapper = objectMapper.readValue(entity.getContent(), RegistryMapper.class);
      List<PackageMapper> packages = new ArrayList<>(registryMapper.getPackages().values());
      List<List<PackageMapper>> chunks = Lists.partition(packages, 100);

      for (List<PackageMapper> partition : chunks) {
        List<RemotePackage> remotePackagePartition = new ArrayList<>();
        for (PackageMapper packageMapper : partition) {

          if (packageMapper.getVersions().containsKey(packageMapper.getLatestVersion())) {
            String version = packageMapper.getLatestVersion();
            PackageVersionMapper latestPackage = packageMapper.getVersions()
                .get(version);

            RemotePackage remotePackage = RegistryModelAdapter.jsonMapperToEntity(latestPackage);
            remotePackage.setSlug(packageMapper.getSlug());
            remotePackage.setRemoteSource(remoteSource);
            remotePackage.setVersion(version);
            remotePackagePartition.add(remotePackage);
          }
        }
        remotePackageRepository.saveAll(remotePackagePartition);
      }

    } catch (Exception e) {
      throw new StoreParsingException(e);
    }
  }

  private void processOASSource(HttpEntity entity, RemoteSource remoteSource) throws StoreParsingException {
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            false);
    try {
      OASRegistry registryMapper = objectMapper.readValue(entity.getContent(), OASRegistry.class);
      List<OASPackage> packages = new ArrayList<>(registryMapper.getPlugins().values());
      List<List<OASPackage>> chunks = Lists.partition(packages, 100);

      for (List<OASPackage> partition : chunks) {
        List<RemotePackage> remotePackagePartition = new ArrayList<>();
        for (OASPackage packageMapper : partition) {

          if (packageMapper.getVersions().containsKey(packageMapper.getVersion())) {
            String version = packageMapper.getVersion();
            OASPlugin latestPlugin = packageMapper.getVersions().get(version);

            RemotePackage remotePackage = OASModelAdapter.mapperToEntity(latestPlugin);
            remotePackage.setSlug(packageMapper.getSlug());
            remotePackage.setRemoteSource(remoteSource);
            remotePackage.setVersion(version);

            // Download details are intentionally dropped for the official Open Audio Stack
            // registry so they get lazily re-fetched from the registry detail endpoint at
            // install time. This validates that BundleInstallTask#resolveMissingFileDetails
            // correctly backfills them. Third-party sources that merely implement the OAS
            // format are left untouched, since they are not guaranteed to expose a working
            // detail endpoint.
            // This can be completely removed once the lazy fetch has been proven reliable
            // and following PR is merged:
            // https://github.com/open-audio-stack/open-audio-stack-registry/pull/933
            if (isOfficialOasRegistry(remoteSource)) {
              clearLazyDownloadDetails(remotePackage);
            }

            // Only add package if at least on bundle is present
            // and if type is known.
            if (!remotePackage.getBundles().isEmpty()
                && remotePackage.getType() != null) {
              remotePackagePartition.add(remotePackage);
            } else {
              log.debug("Skipping OAS package '{}' ({}): {}", packageMapper.getSlug(), version,
                  remotePackage.getBundles().isEmpty() ? "no bundles" : "unresolved plugin type");
            }
          }
        }
        remotePackageRepository.saveAll(remotePackagePartition);
      }

    } catch (Exception e) {
      throw new StoreParsingException(e);
    }
  }

  /**
   * Checks whether the given remote source is the official Open Audio Stack registry, as
   * opposed to a third-party source that only implements the OAS format.
   *
   * @param remoteSource remote source to check
   * @return true if remoteSource is the official OAS registry
   */
  @Deprecated
  private boolean isOfficialOasRegistry(RemoteSource remoteSource) {
    return OFFICIAL_OAS_REGISTRY_URL.equals(remoteSource.getUrl());
  }

  /**
   * Clears the download url and sha256 of every bundle of the given package, forcing them
   * to be lazily re-fetched from the OAS registry detail endpoint at install time.
   *
   * @param remotePackage package whose bundles' download details are cleared
   */
  @Deprecated
  private void clearLazyDownloadDetails(RemotePackage remotePackage) {
    for (PackageBundle bundle : remotePackage.getBundles()) {
      bundle.setDownloadUrl(null);
      bundle.setDownloadSha256(null);
    }
  }

  private static class StoreParsingException extends Exception {
    StoreParsingException(Exception e) {
      super(e);
    }
  }
}
