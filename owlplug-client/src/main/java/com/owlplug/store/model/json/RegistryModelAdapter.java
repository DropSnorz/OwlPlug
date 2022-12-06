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
    //product.setSlug(packageVersionJsonMapper.getSlug());
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

        //TODO : remove version and technicalUid checks as it's not in the spec ?

        // If bundle version is not defined, apply package version.
        if (bundle.getVersion() == null) {
          bundle.setVersion(packageVersionJsonMapper.getVersion());
        }

        // If bundle technical uid is not defined, apply package technical uId.
        if (bundle.getTechnicalUid() == null) {
          bundle.setTechnicalUid(packageVersionJsonMapper.getTechnicalUid());
        }
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
