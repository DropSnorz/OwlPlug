package com.dropsnorz.owlplug.core.engine.plugins.discovery;

import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.model.PluginType;
import java.io.File;

public abstract class NativePluginBuilder {
	
	protected PluginType pluginType;
	
	NativePluginBuilder(PluginType pluginType){
		
		this.pluginType = pluginType;
	}
	
	public abstract Plugin build(File file);
	

}
