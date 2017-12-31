package com.dropsnorz.owlplug.engine.plugins.discovery;

import com.dropsnorz.owlplug.model.OSType;
import com.dropsnorz.owlplug.model.PluginType;

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
