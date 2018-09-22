package com.dropsnorz.owlplug.core.tasks.plugins.discovery;

import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.model.PluginFormat;
import com.dropsnorz.owlplug.core.model.VST2Plugin;
import com.dropsnorz.owlplug.core.model.VST3Plugin;
import java.io.File;
import org.apache.commons.io.FilenameUtils;

public class WindowsPluginBuilder extends NativePluginBuilder {

	WindowsPluginBuilder(PluginFormat pluginFormat) {
		super(pluginFormat);
	}

	@Override
	public Plugin build(File file) {
		
		if (pluginFormat == PluginFormat.VST2) {
			String pluginName = FilenameUtils.removeExtension(file.getName());
			String pluginPath = file.getAbsolutePath().replace("\\", "/");
			return new VST2Plugin(pluginName, pluginPath);
		}
		
		if (pluginFormat == PluginFormat.VST3) {
			String pluginName = FilenameUtils.removeExtension(file.getName());
			String pluginPath = file.getAbsolutePath().replace("\\", "/");
			return new VST3Plugin(pluginName, pluginPath);
		}
		return null;
	}

}
