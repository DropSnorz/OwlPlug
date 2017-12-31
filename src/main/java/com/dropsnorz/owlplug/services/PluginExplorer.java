package com.dropsnorz.owlplug.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.boris.jvst.AEffect;
import org.boris.jvst.VST;
import org.boris.jvst.VSTException;

import com.dropsnorz.owlplug.engine.plugins.discovery.NativePluginBuilder;
import com.dropsnorz.owlplug.engine.plugins.discovery.NativePluginBuilderFactory;
import com.dropsnorz.owlplug.engine.plugins.discovery.NativePluginCollector;
import com.dropsnorz.owlplug.engine.plugins.discovery.NativePluginCollectorFactory;
import com.dropsnorz.owlplug.model.OSType;
import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginType;

public class PluginExplorer {

	protected String VST2Path;
	protected String Platform;

	public PluginExplorer(String platform, String VST2Path){

		this.VST2Path = VST2Path;

	}

	public List<Plugin> explore(){
		
		ArrayList<Plugin> discoveredPlugins = new ArrayList<Plugin>();

		PluginType[] types = {PluginType.VST2};
		NativePluginCollector collector = NativePluginCollectorFactory.getPluginFinder(OSType.WIN, types);
		List<File> files = collector.collect(VST2Path);

		System.out.println("-- WIN VST2 - PLUGINS --");
		
		NativePluginBuilder builder =NativePluginBuilderFactory.createPluginBuilder(OSType.WIN, PluginType.VST2);
		
		for(File file: files){

			System.out.println("LOADING: " + file.getAbsolutePath());
				
			discoveredPlugins.add(builder.build(file));
				
				/*
				AEffect a;

				a = VST.load(file.getAbsolutePath());
				System.out.println(a.getEffectName());
				System.out.println(a.getProgramName());
				System.out.println(a.getVersion());
				
				*/

		}
		
		for(Plugin plugin: discoveredPlugins){
			
			System.out.println(plugin.getName());
		}
		
		return discoveredPlugins;
		
		

	}


}
