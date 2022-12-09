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

package com.owlplug.store.model.json;

import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.PluginStage;
import com.owlplug.core.model.PluginType;
import com.owlplug.core.utils.UrlUtils;
import com.owlplug.store.model.ProductBundle;
import com.owlplug.store.model.ProductTag;
import com.owlplug.store.model.Store;
import com.owlplug.store.model.StoreProduct;

import java.util.HashSet;

public class RegistryModelAdapter {
  /**
   * Creates a {@link Store} entity from a {@link RegistryJsonMapper}.
   *
   * @param registryJsonMapper registry json mapper
   * @return pluginStoreEntity
   */
  public static Store jsonMapperToEntity(RegistryJsonMapper registryJsonMapper) {

    Store store = new Store();
    store.setName(registryJsonMapper.getName());
    store.setUrl(registryJsonMapper.getUrl());
    return store;
  }

  /**
   * Creates a {@link StoreProduct} entity from a {@link PackageVersionJsonMapper}.
   *
   * @param packageVersionJsonMapper product json mapper
   * @return product entity
   */
  public static StoreProduct jsonMapperToEntity(PackageVersionJsonMapper packageVersionJsonMapper) {

    StoreProduct product = new StoreProduct();
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

    HashSet<ProductBundle> bundles = new HashSet<>();
    if (packageVersionJsonMapper.getBundles() != null) {
      for (BundleJsonMapper bundleMapper : packageVersionJsonMapper.getBundles()) {
        ProductBundle bundle = jsonMapperToEntity(bundleMapper);
        bundle.setProduct(product);
        bundles.add(bundle);
      }
    }
    product.setBundles(bundles);

    HashSet<ProductTag> tags = new HashSet<>();
    if (packageVersionJsonMapper.getTags() != null) {
      for (String tag : packageVersionJsonMapper.getTags()) {
        tags.add(new ProductTag(tag, product));
      }
    }
    product.setTags(tags);

    return product;
  }

  /**
   * Creates a {@link ProductBundle} entity from a {@link BundleJsonMapper}.
   *
   * @param bundleMapper bundle json bundleMapper
   * @return bundle entity
   */
  public static ProductBundle jsonMapperToEntity(BundleJsonMapper bundleMapper) {

    ProductBundle productBundle = new ProductBundle();
    productBundle.setName(bundleMapper.getName());
    productBundle.setDownloadUrl(bundleMapper.getDownloadUrl());
    productBundle.setTargets(bundleMapper.getTargets());
    productBundle.setFileSize(bundleMapper.getFileSize());

    if ("vst3".equals(bundleMapper.getFormat())) {
      productBundle.setFormat(PluginFormat.VST3);
    } else if ("au".equals(bundleMapper.getFormat())) {
      productBundle.setFormat(PluginFormat.AU);
    } else if ("lv2".equals(bundleMapper.getFormat())) {
      productBundle.setFormat(PluginFormat.LV2);
    } else {
      productBundle.setFormat(PluginFormat.VST2);
    }

    return productBundle;
  }


}
