package com.dropsnorz.owlplug.services;

import java.io.File;
import java.util.List;

import org.boris.jvst.AEffect;
import org.boris.jvst.VST;
import org.boris.jvst.VSTException;

import com.dropsnorz.owlplug.model.OSType;
import com.dropsnorz.owlplug.model.PluginType;
import com.dropsnorz.owlplug.services.plugins.NativePluginCollector;
import com.dropsnorz.owlplug.services.plugins.NativePluginCollectorFactory;

public class PluginExplorer {

	protected String VST2Path;
	protected String Platform;

	public PluginExplorer(String platform, String VST2Path){

		this.VST2Path = VST2Path;

	}

	public void explore(){

		PluginType[] types = {PluginType.VST2};
		NativePluginCollector collector = NativePluginCollectorFactory.getPluginFinder(OSType.WIN, types);
		List<File> files = collector.collect(VST2Path);

		System.out.println("-- WIN VST2 - PLUGINS --");
		for(File file: files){

			System.out.println("LOADING: " + file.getAbsolutePath());

			AEffect a;
			try {
				a = VST.load(file.getAbsolutePath());
				
				System.out.println(a.getEffectName());
				System.out.println(a.getProgramName());
				System.out.println(a.getVersion());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

		collector = NativePluginCollectorFactory.getPluginFinder(OSType.MAC, types);
		files = collector.collect(VST2Path);

		System.out.println("-- OSX VST2 - PLUGINS --");
		for(File file: files){
			System.out.println(file.getAbsolutePath());
		}


	}


}
