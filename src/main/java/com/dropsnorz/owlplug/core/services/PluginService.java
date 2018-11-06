package com.dropsnorz.owlplug.core.services;

import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.dropsnorz.owlplug.core.dao.PluginDAO;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.utils.PluginUtils;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.dropsnorz.owlplug.store.service.StoreService;
import com.google.common.collect.Iterables;
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
	protected TaskFactory taskFactory;


	public void syncPlugins() {
		taskFactory.createPluginSyncTask().run();
	}

	
	/**
	 * Returns an url to retrieve plugin screenshots.
	 * Url can be retrieve from registered products in store or using OwlPlug Central
	 * screenshot API.
	 * @param plugin the plugin
	 * @return screenshot url
	 */
	public String resolveImageUrl(Plugin plugin) {
		
		String url = "";
		
		String absoluteName = PluginUtils.absoluteName(plugin.getName());
		Iterable<StoreProduct> products = pluginStoreService.getProductsByName(absoluteName);
		
		if (!Iterables.isEmpty(products)) {
			url = Iterables.get(products, 0).getIconUrl();
		} else {
			url = owlplugCentralService.getPluginImageUrl(absoluteName);		
		}
		
		return url;
		
	}


}
