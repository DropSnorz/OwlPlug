package com.owlplug.core.services;

import com.google.common.collect.Iterables;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.dao.PluginDAO;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.utils.PluginUtils;
import com.owlplug.store.model.StoreProduct;
import com.owlplug.store.services.StoreService;
import java.util.prefs.Preferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PluginService {

  @Autowired
  protected OwlPlugCentralService owlplugCentralService;
  @Autowired
  protected StoreService pluginStoreService;
  @Autowired
  protected ApplicationDefaults applicationDefaults;
  @Autowired
  protected Preferences prefs;
  @Autowired
  protected PluginDAO pluginDAO;
  @Autowired
  protected CoreTaskFactory taskFactory;

  public void syncPlugins() {
    taskFactory.createPluginSyncTask().schedule();
  }

  /**
   * Returns an url to retrieve plugin screenshots. Url can be retrieve from
   * registered products in store or using OwlPlug Central screenshot API.
   * 
   * @param plugin the plugin
   * @return screenshot url
   */
  public String resolveImageUrl(Plugin plugin) {

    String url = "";

    String absoluteName = PluginUtils.absoluteName(plugin.getName());
    Iterable<StoreProduct> products = pluginStoreService.getProductsByName(absoluteName);

    if (!Iterables.isEmpty(products)) {
      url = Iterables.get(products, 0).getScreenshotUrl();
    } else {
      url = owlplugCentralService.getPluginImageUrl(absoluteName);
    }

    return url;

  }
  
  /**
   * Removes a plugin reference from database.
   * @param plugin - plugin to remoeve
   */
  public void delete(Plugin plugin) {
    pluginDAO.delete(plugin);
  }
  
  public Plugin save(Plugin plugin) {
    return pluginDAO.save(plugin);
  }
}
