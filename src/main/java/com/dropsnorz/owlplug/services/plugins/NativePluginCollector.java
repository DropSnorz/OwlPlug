package com.dropsnorz.owlplug.services.plugins;

import java.io.File;
import java.util.List;

public interface NativePluginCollector {
	
	public abstract List<File> collect (String path);

}
