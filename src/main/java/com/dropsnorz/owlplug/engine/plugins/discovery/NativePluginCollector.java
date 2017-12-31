package com.dropsnorz.owlplug.engine.plugins.discovery;

import java.io.File;
import java.util.List;

public interface NativePluginCollector {
	
	public abstract List<File> collect (String path);

}
