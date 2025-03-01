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
 
package com.owlplug.explore.model.json.legacy;

import com.owlplug.core.model.PluginStage;
import com.owlplug.core.model.PluginType;
import com.owlplug.core.utils.UrlUtils;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.PackageTag;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.model.json.BundleJsonMapper;
import java.util.HashSet;
import java.util.List;

public class StoreModelAdapter {

  /**
   * Creates a {@link RemoteSource} entity from a {@link StoreJsonMapper}.
   * 
   * @param storeJsonMapper pluginStore json mapper
   * @return pluginStoreEntity
   */
  public static RemoteSource jsonMapperToEntity(StoreJsonMapper storeJsonMapper) {

    RemoteSource remoteSource = new RemoteSource();
    remoteSource.setName(storeJsonMapper.getName());
    remoteSource.setDisplayUrl(storeJsonMapper.getUrl());
    return remoteSource;
  }

  /**
   * Creates a {@link RemotePackage} entity from a {@link ProductJsonMapper}.
   * 
   * @param productJsonMapper product json mapper
   * @return product entity
   */
  public static RemotePackage jsonMapperToEntity(ProductJsonMapper productJsonMapper) {

    RemotePackage remotePackage = new RemotePackage();
    remotePackage.setName(productJsonMapper.getName());
    remotePackage.setSlug(productJsonMapper.getSlug());
    remotePackage.setPageUrl(productJsonMapper.getPageUrl());
    remotePackage.setDownloadUrl(UrlUtils.fixSpaces(productJsonMapper.getDownloadUrl()));
    remotePackage.setScreenshotUrl(UrlUtils.fixSpaces(productJsonMapper.getScreenshotUrl()));
    remotePackage.setDonateUrl(UrlUtils.fixSpaces(productJsonMapper.getDonateUrl()));
    remotePackage.setCreator(productJsonMapper.getCreator());
    remotePackage.setLicense(productJsonMapper.getLicense());
    remotePackage.setVersion(productJsonMapper.getVersion());
    remotePackage.setDescription(productJsonMapper.getDescription());

    if (productJsonMapper.getType() != null) {
      remotePackage.setType(PluginType.fromString(productJsonMapper.getType()));
    }

    if (productJsonMapper.getStage() != null) {
      remotePackage.setStage(PluginStage.fromString(productJsonMapper.getStage()));
    }

    HashSet<PackageBundle> bundles = new HashSet<>();
    if (productJsonMapper.getBundles() != null) {
      for (BundleJsonMapper bundleMapper : productJsonMapper.getBundles()) {
        PackageBundle bundle = jsonMapperToEntity(bundleMapper);
        
        // If bundle version is not defined, apply product version.
        if (bundle.getVersion() == null) {
          bundle.setVersion(productJsonMapper.getVersion());
        }
        // If bundle technical uid is not defined, apply product technical uId.
        if (bundle.getTechnicalUid() == null) {
          bundle.setTechnicalUid(productJsonMapper.getTechnicalUid());
        }
        bundle.setRemotePackage(remotePackage);
        bundles.add(bundle);
      }
    }
    remotePackage.setBundles(bundles);

    HashSet<PackageTag> tags = new HashSet<>();
    if (productJsonMapper.getTags() != null) {
      for (String tag : productJsonMapper.getTags()) {
        tags.add(new PackageTag(tag, remotePackage));
      }
    }
    remotePackage.setTags(tags);

    return remotePackage;
  }

  /**
   * Creates a {@link PackageBundle} entity from a {@link BundleJsonMapper}.
   * 
   * @param mapper bundle json mapper
   * @return bundle entity
   */
  public static PackageBundle jsonMapperToEntity(BundleJsonMapper mapper) {

    PackageBundle packageBundle = new PackageBundle();
    packageBundle.setName(mapper.getName());
    packageBundle.setDownloadUrl(mapper.getDownloadUrl());
    packageBundle.setDownloadSha256(mapper.getDownloadSha256());
    packageBundle.setTargets(mapper.getTargets());
    packageBundle.setFileSize(mapper.getFileSize());

    if ("vst3".equals(mapper.getFormat())) {
      packageBundle.setFormats(List.of("vst3"));
    } else if ("au".equals(mapper.getFormat())) {
      packageBundle.setFormats(List.of("au"));
    } else if ("lv2".equals(mapper.getFormat())) {
      packageBundle.setFormats(List.of("lv2"));
    } else {
      packageBundle.setFormats(List.of("vst"));
    }

    return packageBundle;
  }

}
