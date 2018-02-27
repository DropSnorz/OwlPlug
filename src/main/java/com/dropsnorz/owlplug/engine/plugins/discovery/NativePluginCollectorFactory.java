package com.dropsnorz.owlplug.engine.plugins.discovery;

import com.dropsnorz.owlplug.model.OSType;
import com.dropsnorz.owlplug.model.PluginType;

public class NativePluginCollectorFactory {
	
	public static NativePluginCollector getPluginFinder(OSType platform, PluginType type){
		
		switch(platform){
			case WIN: return new WindowsPluginCollector(type);
			case MAC: return new OSXPluginCollector(type);
		default: break;
		}
		return null;
	}

}
