package com.dropsnorz.owlplug.engine.plugins.discovery;

import java.io.File;

import com.dropsnorz.owlplug.model.OSType;
import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginType;

public abstract class NativePluginBuilder {
	
	PluginType pluginType;
	
	NativePluginBuilder(PluginType pluginType){
		
		this.pluginType = pluginType;
	}
	
	public abstract Plugin build(File file);
	

}
