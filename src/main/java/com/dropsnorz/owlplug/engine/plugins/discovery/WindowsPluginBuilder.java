package com.dropsnorz.owlplug.engine.plugins.discovery;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginType;
import com.dropsnorz.owlplug.model.VST2Plugin;
import com.dropsnorz.owlplug.model.VST3Plugin;

public class WindowsPluginBuilder extends NativePluginBuilder {

	WindowsPluginBuilder(PluginType pluginType) {
		super(pluginType);
	}

	@Override
	public Plugin build(File file) {
		
		if(pluginType == PluginType.VST2){
			String pluginName = FilenameUtils.removeExtension(file.getName());
			String pluginPath = file.getAbsolutePath().replace("\\", "/");
			return new VST2Plugin(pluginName, pluginPath);
		}
		
		if(pluginType == PluginType.VST3){
			String pluginName = FilenameUtils.removeExtension(file.getName());
			String pluginPath = file.getAbsolutePath().replace("\\", "/");
			return new VST3Plugin(pluginName, pluginPath);
		}
		
		return null;
	}

}
