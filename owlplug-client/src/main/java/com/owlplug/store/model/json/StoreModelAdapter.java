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
    } else {
      productBundle.setFormat(PluginFormat.VST2);
    }

    return productBundle;
  }

}
