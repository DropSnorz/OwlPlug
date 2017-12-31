package com.dropsnorz.owlplug.engine.plugins.discovery;

import java.io.File;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginType;

public class OSXPluginBuilder extends NativePluginBuilder {

	OSXPluginBuilder(PluginType pluginType) {
		super(pluginType);
	}

	@Override
	public Plugin build(File file) {
		
		if(pluginType == PluginType.VST2){
			return new Plugin(file.getName());
		}
		
		return null;
	}

}
