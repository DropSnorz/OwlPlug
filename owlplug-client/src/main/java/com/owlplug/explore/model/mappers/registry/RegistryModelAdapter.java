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

package com.owlplug.explore.model.mappers.registry;

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
   * Creates a {@link RemoteSource} entity from a {@link RegistryMapper}.
   *
   * @param registryMapper registry json mapper
   * @return remoteSourceEntity
   */
  public static RemoteSource jsonMapperToEntity(RegistryMapper registryMapper) {

    RemoteSource remoteSource = new RemoteSource();
    remoteSource.setName(registryMapper.getName());
    remoteSource.setDisplayUrl(registryMapper.getUrl());
    return remoteSource;
  }

  /**
   * Creates a {@link RemotePackage} entity from a {@link PackageVersionMapper}.
   *
   * @param packageVersionMapper product json mapper
   * @return product entity
   */
  public static RemotePackage jsonMapperToEntity(PackageVersionMapper packageVersionMapper) {
    RemotePackage remotePackage = new RemotePackage();
    remotePackage.setName(packageVersionMapper.getName());
    remotePackage.setPageUrl(packageVersionMapper.getPageUrl());
    remotePackage.setScreenshotUrl(UrlUtils.fixSpaces(packageVersionMapper.getScreenshotUrl()));
    remotePackage.setDonateUrl(UrlUtils.fixSpaces(packageVersionMapper.getDonateUrl()));
    remotePackage.setCreator(packageVersionMapper.getCreator());
    remotePackage.setLicense(packageVersionMapper.getLicense());
    remotePackage.setDescription(packageVersionMapper.getDescription());

    if (packageVersionMapper.getType() != null) {
      remotePackage.setType(PluginType.fromString(packageVersionMapper.getType()));
    }

    if (packageVersionMapper.getStage() != null) {
      remotePackage.setStage(PluginStage.fromString(packageVersionMapper.getStage()));
    }

    HashSet<PackageBundle> bundles = new HashSet<>();
    if (packageVersionMapper.getBundles() != null) {
      for (BundleMapper bundleMapper : packageVersionMapper.getBundles()) {
        PackageBundle bundle = jsonMapperToEntity(bundleMapper);
        bundle.setRemotePackage(remotePackage);
        bundles.add(bundle);
      }
    }
    remotePackage.setBundles(bundles);

    HashSet<PackageTag> tags = new HashSet<>();
    if (packageVersionMapper.getTags() != null) {
      for (String tag : packageVersionMapper.getTags()) {
        tags.add(new PackageTag(tag, remotePackage));
      }
    }
    remotePackage.setTags(tags);

    return remotePackage;
  }

  /**
   * Creates a {@link PackageBundle} entity from a {@link BundleMapper}.
   *
   * @param bundleMapper bundle json bundleMapper
   * @return bundle entity
   */
  public static PackageBundle jsonMapperToEntity(BundleMapper bundleMapper) {

    PackageBundle packageBundle = new PackageBundle();
    packageBundle.setName(bundleMapper.getName());
    packageBundle.setDownloadUrl(bundleMapper.getDownloadUrl());
    packageBundle.setDownloadSha256(bundleMapper.getDownloadSha256());
    packageBundle.setTargets(bundleMapper.getTargets());
    packageBundle.setFileSize(bundleMapper.getFileSize());

    if (bundleMapper.getFormats() != null && bundleMapper.getFormats().size() > 0) {
      List<String> formats = new ArrayList<>(bundleMapper.getFormats());
      formats.replaceAll(e -> e.equals("vst") ? "vst2" : e.toLowerCase());
      packageBundle.setFormats(formats);

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
