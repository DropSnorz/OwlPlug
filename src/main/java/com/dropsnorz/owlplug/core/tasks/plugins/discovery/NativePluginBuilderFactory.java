package com.dropsnorz.owlplug.core.tasks.plugins.discovery;

import com.dropsnorz.owlplug.core.model.OSType;
import com.dropsnorz.owlplug.core.model.PluginType;

public class NativePluginBuilderFactory {

	public static NativePluginBuilder createPluginBuilder(OSType osType, PluginType pluginType){

		switch(osType){
			case WIN: return new WindowsPluginBuilder(pluginType);
			case MAC: return new OSXPluginBuilder(pluginType);
			default: break;
		}

		return null;

	}

}
