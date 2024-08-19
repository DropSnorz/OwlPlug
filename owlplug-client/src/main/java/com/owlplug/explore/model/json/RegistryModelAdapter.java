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

package com.owlplug.explore.model.json;

import com.owlplug.core.model.PluginStage;
import com.owlplug.core.model.PluginType;
import com.owlplug.core.utils.UrlUtils;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.PackageTag;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.RemoteSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RegistryModelAdapter {
  /**
   * Creates a {@link RemoteSource} entity from a {@link RegistryJsonMapper}.
   *
   * @param registryJsonMapper registry json mapper
   * @return pluginStoreEntity
   */
  public static RemoteSource jsonMapperToEntity(RegistryJsonMapper registryJsonMapper) {

    RemoteSource remoteSource = new RemoteSource();
    remoteSource.setName(registryJsonMapper.getName());
    remoteSource.setDisplayUrl(registryJsonMapper.getUrl());
    return remoteSource;
  }

  /**
   * Creates a {@link RemotePackage} entity from a {@link PackageVersionJsonMapper}.
   *
   * @param packageVersionJsonMapper product json mapper
   * @return product entity
   */
  public static RemotePackage jsonMapperToEntity(PackageVersionJsonMapper packageVersionJsonMapper) {

    RemotePackage product = new RemotePackage();
    product.setName(packageVersionJsonMapper.getName());
    product.setPageUrl(packageVersionJsonMapper.getPageUrl());
    product.setScreenshotUrl(UrlUtils.fixSpaces(packageVersionJsonMapper.getScreenshotUrl()));
    product.setDonateUrl(UrlUtils.fixSpaces(packageVersionJsonMapper.getDonateUrl()));
    product.setCreator(packageVersionJsonMapper.getCreator());
    product.setLicense(packageVersionJsonMapper.getLicense());
    product.setDescription(packageVersionJsonMapper.getDescription());

    if (packageVersionJsonMapper.getType() != null) {
      product.setType(PluginType.fromString(packageVersionJsonMapper.getType()));
    }

    if (packageVersionJsonMapper.getStage() != null) {
      product.setStage(PluginStage.fromString(packageVersionJsonMapper.getStage()));
    }

    HashSet<PackageBundle> bundles = new HashSet<>();
    if (packageVersionJsonMapper.getBundles() != null) {
      for (BundleJsonMapper bundleMapper : packageVersionJsonMapper.getBundles()) {
        PackageBundle bundle = jsonMapperToEntity(bundleMapper);
        bundle.setRemotePackage(product);
        bundles.add(bundle);
      }
    }
    product.setBundles(bundles);

    HashSet<PackageTag> tags = new HashSet<>();
    if (packageVersionJsonMapper.getTags() != null) {
      for (String tag : packageVersionJsonMapper.getTags()) {
        tags.add(new PackageTag(tag, product));
      }
    }
    product.setTags(tags);

    return product;
  }

  /**
   * Creates a {@link PackageBundle} entity from a {@link BundleJsonMapper}.
   *
   * @param bundleMapper bundle json bundleMapper
   * @return bundle entity
   */
  public static PackageBundle jsonMapperToEntity(BundleJsonMapper bundleMapper) {

    PackageBundle packageBundle = new PackageBundle();
    packageBundle.setName(bundleMapper.getName());
    packageBundle.setDownloadUrl(bundleMapper.getDownloadUrl());
    packageBundle.setDownloadSha256(bundleMapper.getDownloadSha256());
    packageBundle.setTargets(bundleMapper.getTargets());
    packageBundle.setFileSize(bundleMapper.getFileSize());

    if (bundleMapper.getFormats() != null && bundleMapper.getFormats().size() > 0) {
      packageBundle.setFormats(bundleMapper.getFormats());

    // Support undefined formats field with fallback to format
    } else if (bundleMapper.getFormat() != null) {
      List<String> formats = new ArrayList<>();
      formats.add(bundleMapper.getFormat());
      formats.replaceAll(e -> e.equals("vst") ? "vst2" : e.toLowerCase());
      packageBundle.setFormats(formats);
    } else {
      packageBundle.setFormats(List.of("vst2"));
    }

    return packageBundle;
  }


}
