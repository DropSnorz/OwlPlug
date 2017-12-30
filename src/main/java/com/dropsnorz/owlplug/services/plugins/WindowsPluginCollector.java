package com.dropsnorz.owlplug.services.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.dropsnorz.owlplug.model.PluginType;

public class WindowsPluginCollector implements NativePluginCollector {

	PluginType[] pluginTypes;
	ArrayList<File> fileList = new ArrayList<File>();

	public WindowsPluginCollector(PluginType[] types) {

		this.pluginTypes  = types;

	}

	@Override
	public List<File> collect(String path) {

		File dir = new File(path);
		List<File> baseFiles = (List<File>) FileUtils.listFiles(dir,  TrueFileFilter.TRUE,  TrueFileFilter.TRUE);

		for(File file: baseFiles){
			
			for(PluginType pluginType : pluginTypes){

				if(pluginType == PluginType.VST2){
					if (file.getAbsolutePath().endsWith(".dll") && file.isFile()) fileList.add(file);
				}
				else if (pluginType == PluginType.VST3){
					if (file.getAbsolutePath().endsWith(".vst3")) fileList.add(file);

				}
			}

		}

		return fileList;
	}

}
