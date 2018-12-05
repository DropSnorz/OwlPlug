package com.dropsnorz.owlplug.core.tasks.plugins.discovery;

import com.dropsnorz.owlplug.core.model.PluginFormat;
import com.dropsnorz.owlplug.core.model.platform.OperatingSystem;

public class NativePluginCollectorFactory {

	public static NativePluginCollector getPluginFinder(OperatingSystem platform, PluginFormat pluginFormat) {

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
