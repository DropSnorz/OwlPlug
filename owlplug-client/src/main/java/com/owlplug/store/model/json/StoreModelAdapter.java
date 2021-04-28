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

public class StoreModelAdapter {

  /**
   * Crestes a {@link Store} entity from a {@link StoreJsonMapper}.
   * 
   * @param storeJsonMapper pluginStore json mapper
   * @return pluginStoreEntity
   */
  public static Store jsonMapperToEntity(StoreJsonMapper storeJsonMapper) {

    Store store = new Store();
    store.setName(storeJsonMapper.getName());
    store.setUrl(storeJsonMapper.getUrl());
    return store;
  }

  /**
   * Creates a {@link StoreProduct} entity from a {@link ProductJsonMapper}.
   * 
   * @param productJsonMapper product json mapper
   * @return product entity
   */
  public static StoreProduct jsonMapperToEntity(ProductJsonMapper productJsonMapper) {

    StoreProduct product = new StoreProduct();
    product.setName(productJsonMapper.getName());
    product.setPageUrl(productJsonMapper.getPageUrl());
    product.setDownloadUrl(UrlUtils.fixSpaces(productJsonMapper.getDownloadUrl()));
    product.setScreenshotUrl(UrlUtils.fixSpaces(productJsonMapper.getScreenshotUrl()));
    product.setDonateUrl(UrlUtils.fixSpaces(productJsonMapper.getDonateUrl()));
    product.setCreator(productJsonMapper.getCreator());
    product.setVersion(productJsonMapper.getVersion());
    product.setDescription(productJsonMapper.getDescription());

    if (productJsonMapper.getType() != null) {
      product.setType(PluginType.fromString(productJsonMapper.getType()));
    }

    if (productJsonMapper.getStage() != null) {
      product.setStage(PluginStage.fromString(productJsonMapper.getStage()));
    }

    HashSet<ProductBundle> bundles = new HashSet<>();
    if (productJsonMapper.getBundles() != null) {
      for (BundleJsonMapper bundleMapper : productJsonMapper.getBundles()) {
        ProductBundle bundle = jsonMapperToEntity(bundleMapper);
        
        // If bundle version is not defined, apply product version.
        if (bundle.getVersion() == null) {
          bundle.setVersion(productJsonMapper.getVersion());
        }
        // If bundle technical uid is not defined, apply product technical uId.
        if (bundle.getTechnicalUid() == null) {
          bundle.setTechnicalUid(productJsonMapper.getTechnicalUid());
        }
        bundle.setProduct(product);
        bundles.add(bundle);
      }
    }
    product.setBundles(bundles);

    HashSet<ProductTag> tags = new HashSet<>();
    if (productJsonMapper.getTags() != null) {
      for (String tag : productJsonMapper.getTags()) {
        tags.add(new ProductTag(tag, product));
      }
    }
    product.setTags(tags);

    return product;
  }

  /**
   * Creates a {@link ProductBundle} entity from a {@link BundleJsonMapper}.
   * 
   * @param mapper bundle json mapper
   * @return bundle entity
   */
  public static ProductBundle jsonMapperToEntity(BundleJsonMapper mapper) {

    ProductBundle productBundle = new ProductBundle();
    productBundle.setName(mapper.getName());
    productBundle.setDownloadUrl(mapper.getDownloadUrl());
    productBundle.setTargets(mapper.getTargets());
    productBundle.setFileSize(mapper.getFileSize());

    if ("vst3".equals(mapper.getFormat())) {
      productBundle.setFormat(PluginFormat.VST3);
    } else if ("au".equals(mapper.getFormat())) {
    	productBundle.setFormat(PluginFormat.AU);
    } else {
      productBundle.setFormat(PluginFormat.VST2);
    }

    return productBundle;
  }

}
