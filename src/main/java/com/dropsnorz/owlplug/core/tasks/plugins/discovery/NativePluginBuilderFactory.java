package com.dropsnorz.owlplug.core.tasks.plugins.discovery;

import com.dropsnorz.owlplug.core.model.OSType;
import com.dropsnorz.owlplug.core.model.PluginFormat;

public class NativePluginBuilderFactory {

	public static NativePluginBuilder createPluginBuilder(OSType osType, PluginFormat pluginFormat){

		switch(osType){
			case WIN: return new WindowsPluginBuilder(pluginFormat);
			case MAC: return new OSXPluginBuilder(pluginFormat);
			default: break;
		}

		return null;

	}

}
