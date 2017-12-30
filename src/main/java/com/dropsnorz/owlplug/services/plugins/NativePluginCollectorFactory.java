package com.dropsnorz.owlplug.services.plugins;

import com.dropsnorz.owlplug.model.OSType;
import com.dropsnorz.owlplug.model.PluginType;

public class NativePluginCollectorFactory {
	
	public static NativePluginCollector getPluginFinder(OSType platform, PluginType[] types){
		
		switch(platform){
			case WIN: return new WindowsPluginCollector(types);
			case MAC: return new OSXPluginCollector(types);
		default: break;
		}
		return null;
	}

}
