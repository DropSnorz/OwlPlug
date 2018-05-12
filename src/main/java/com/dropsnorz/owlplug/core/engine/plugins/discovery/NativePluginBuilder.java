package com.dropsnorz.owlplug.core.engine.plugins.discovery;

import java.io.File;

import com.dropsnorz.owlplug.core.model.OSType;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.model.PluginType;

public abstract class NativePluginBuilder {
	
	PluginType pluginType;
	
	NativePluginBuilder(PluginType pluginType){
		
		this.pluginType = pluginType;
	}
	
	public abstract Plugin build(File file);
	

}
