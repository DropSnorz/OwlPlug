package com.dropsnorz.owlplug.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.boris.jvst.AEffect;
import org.boris.jvst.VST;
import org.boris.jvst.VSTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.engine.plugins.discovery.NativePluginBuilder;
import com.dropsnorz.owlplug.engine.plugins.discovery.NativePluginBuilderFactory;
import com.dropsnorz.owlplug.engine.plugins.discovery.NativePluginCollector;
import com.dropsnorz.owlplug.engine.plugins.discovery.NativePluginCollectorFactory;
import com.dropsnorz.owlplug.model.OSType;
import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginType;

@Service
public class PluginExplorer{

	@Autowired
	protected ApplicationDefaults applicationDefaults;

	@Autowired
	protected Preferences prefs;


	public List<Plugin> explore(){



		OSType platform = applicationDefaults.getPlatform();

		ArrayList<Plugin> discoveredPlugins = new ArrayList<Plugin>();


		if(prefs.getBoolean("VST2_DISCOVERY_ENABLED", false)) {
			
			List<File> vst2files = new ArrayList<File>();


			String vst2path = prefs.get("VST2_DIRECTORY", "");
			NativePluginCollector collector = NativePluginCollectorFactory.getPluginFinder(platform, PluginType.VST2);
			vst2files = collector.collect(vst2path);

			NativePluginBuilder builder = NativePluginBuilderFactory.createPluginBuilder(platform, PluginType.VST2);

			for(File file: vst2files){

				discoveredPlugins.add(builder.build(file));

				/*
					AEffect a;

					a = VST.load(file.getAbsolutePath());
					System.out.println(a.getEffectName());
					System.out.println(a.getProgramName());
					System.out.println(a.getVersion());

				 */

			}


		}


		return discoveredPlugins;

	}

}
