package com.dropsnorz.owlplug.core.engine.plugins.discovery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropsnorz.owlplug.core.model.PluginType;

public class WindowsPluginCollector implements NativePluginCollector {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());


	PluginType pluginType;
	ArrayList<File> fileList = new ArrayList<>();

	public WindowsPluginCollector(PluginType type) {

		this.pluginType  = type;

	}

	@Override
	public List<File> collect(String path) {

		File dir = new File(path);
		
		if(dir.isDirectory()) {
			
			List<File> baseFiles = (List<File>) FileUtils.listFiles(dir,  TrueFileFilter.TRUE,  TrueFileFilter.TRUE);

			for(File file: baseFiles){

				if(pluginType == PluginType.VST2){
					if (file.getAbsolutePath().endsWith(".dll") && file.isFile()) fileList.add(file);
				}
				else if (pluginType == PluginType.VST3){
					if (file.getAbsolutePath().endsWith(".vst3")) fileList.add(file);

				}
			}
		}
		else {
			log.error("Plugin root is not a directory");
		}

		return fileList;
	}

}
