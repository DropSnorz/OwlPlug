package com.dropsnorz.owlplug.core.engine.plugins.discovery;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.model.PluginType;
import com.dropsnorz.owlplug.core.model.VST2Plugin;
import com.dropsnorz.owlplug.core.model.VST3Plugin;

public class OSXPluginBuilder extends NativePluginBuilder {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	OSXPluginBuilder(PluginType pluginType) {
		super(pluginType);
	}

	@Override
	public Plugin build(File pluginFile) {

		if(pluginType == PluginType.VST2){
			return buildVst2Plugin(pluginFile);
		}

		return null;
	}

	private Plugin buildVst2Plugin(File pluginFile) {

		String pluginName = FilenameUtils.removeExtension(pluginFile.getName());
		String pluginPath = pluginFile.getAbsolutePath().replace("\\", "/");

		Plugin plugin = new VST2Plugin(pluginName, pluginPath);

		File pList = new File(pluginFile.getAbsolutePath() + "/Contents/Info.plist");
		if(pList.exists()) {
			buildPluginFromVST2Plist(plugin, pList);
		}


		return plugin;
	}

	private Plugin buildVST3Plugin(File pluginFile) {
		String pluginName = FilenameUtils.removeExtension(pluginFile.getName());
		String pluginPath = pluginFile.getAbsolutePath().replace("\\", "/");

		return new VST3Plugin(pluginName, pluginPath);
	}

	private void buildPluginFromVST2Plist(Plugin plugin, File pList) {

		try {
			NSDictionary rootDict = (NSDictionary)PropertyListParser.parse(pList);
			NSObject nSBundleId = rootDict.objectForKey("CFBundleIdentifier");
			NSObject nSBundleVersion = rootDict.objectForKey("CFBundleShortVersionString");

			if(nSBundleVersion == null) {
				nSBundleVersion = rootDict.objectForKey("CFBundleVersion");
			}

			if(nSBundleId != null) {
				plugin.setBundleId(nSBundleId.toString());
			}

			if(nSBundleVersion != null) {
				plugin.setVersion(nSBundleVersion.toString());
			}


		} catch (IOException | PropertyListFormatException | ParseException | ParserConfigurationException
				| SAXException e) {
			log.error("Error while building plugin from Plistfile", e);

		}	

	}

}
