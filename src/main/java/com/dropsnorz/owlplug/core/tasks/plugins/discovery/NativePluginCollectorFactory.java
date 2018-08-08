package com.dropsnorz.owlplug.core.tasks.plugins.discovery;

import com.dropsnorz.owlplug.core.model.OSType;
import com.dropsnorz.owlplug.core.model.PluginType;

public class NativePluginCollectorFactory {

	public static NativePluginCollector getPluginFinder(OSType platform, PluginType type) {

		switch (platform) {
			case WIN: 
				return new WindowsPluginCollector(type);
			case MAC: 
				return new OSXPluginCollector(type);
			default: 
				break;
		}
		return null;
	}

}
