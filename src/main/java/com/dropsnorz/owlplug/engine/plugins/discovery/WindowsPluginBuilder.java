package com.dropsnorz.owlplug.engine.plugins.discovery;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginType;

public class WindowsPluginBuilder extends NativePluginBuilder {

	WindowsPluginBuilder(PluginType pluginType) {
		super(pluginType);
	}

	@Override
	public Plugin build(File file) {
		
		if(pluginType == PluginType.VST2){
			String pluginName = FilenameUtils.removeExtension(file.getName());
			return new Plugin(pluginName);
		}
		
		return null;
	}

}
