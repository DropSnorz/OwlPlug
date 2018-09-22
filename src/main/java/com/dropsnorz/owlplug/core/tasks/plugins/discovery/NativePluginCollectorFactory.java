package com.dropsnorz.owlplug.core.tasks.plugins.discovery;

import com.dropsnorz.owlplug.core.model.OSType;
import com.dropsnorz.owlplug.core.model.PluginFormat;

public class NativePluginCollectorFactory {

	public static NativePluginCollector getPluginFinder(OSType platform, PluginFormat pluginFormat) {

		switch (platform) {
			case WIN: 
				return new WindowsPluginCollector(pluginFormat);
			case MAC: 
				return new OSXPluginCollector(pluginFormat);
			default: 
				break;
		}
		return null;
	}

}
