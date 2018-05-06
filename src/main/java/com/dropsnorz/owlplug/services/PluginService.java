package com.dropsnorz.owlplug.services;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.dao.PluginDAO;
import com.dropsnorz.owlplug.engine.plugins.discovery.NativePluginBuilder;
import com.dropsnorz.owlplug.engine.plugins.discovery.NativePluginBuilderFactory;
import com.dropsnorz.owlplug.engine.plugins.discovery.NativePluginCollector;
import com.dropsnorz.owlplug.engine.plugins.discovery.NativePluginCollectorFactory;
import com.dropsnorz.owlplug.model.OSType;
import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginType;

@Service
public class PluginService {

	@Autowired
	protected ApplicationDefaults applicationDefaults;

	@Autowired
	protected Preferences prefs;

	@Autowired
	protected PluginDAO pluginDAO;

	@Autowired
	protected TaskFactory taskFactory;

	public void removePlugin(Plugin plugin) {

		taskFactory.run(taskFactory.createPluginRemoveTask(plugin));

	}

	public List<Plugin> explore(){

		OSType platform = applicationDefaults.getPlatform();
		ArrayList<Plugin> discoveredPlugins = new ArrayList<Plugin>();


		try {
			if(prefs.getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false)) {

				List<File> vst2files = new ArrayList<File>();

				String vst2path = prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, "");
				NativePluginCollector collector = NativePluginCollectorFactory.getPluginFinder(platform, PluginType.VST2);
				vst2files = collector.collect(vst2path);

				NativePluginBuilder builder = NativePluginBuilderFactory.createPluginBuilder(platform, PluginType.VST2);

				for(File file: vst2files){

					discoveredPlugins.add(builder.build(file));

				}

			}
			if(prefs.getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false)) {

				List<File> vst3files = new ArrayList<File>();

				String vst3path = prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, "");
				NativePluginCollector collector = NativePluginCollectorFactory.getPluginFinder(platform, PluginType.VST3);
				vst3files = collector.collect(vst3path);

				NativePluginBuilder builder = NativePluginBuilderFactory.createPluginBuilder(platform, PluginType.VST3);

				for(File file: vst3files){

					discoveredPlugins.add(builder.build(file));

				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return discoveredPlugins;

	}



}
