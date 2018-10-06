package com.dropsnorz.owlplug.core.tasks.plugins.discovery;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.model.PluginFormat;
import com.dropsnorz.owlplug.core.model.VST2Plugin;
import com.dropsnorz.owlplug.core.model.VST3Plugin;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class OSXPluginBuilder extends NativePluginBuilder {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	OSXPluginBuilder(PluginFormat pluginFormat) {
		super(pluginFormat);
	}

	@Override
	public Plugin build(File pluginFile) {

		if (pluginFormat == PluginFormat.VST2) {
			return buildVst2Plugin(pluginFile);
		}
		return null;
	}

	private Plugin buildVst2Plugin(File pluginFile) {

		String pluginName = FilenameUtils.removeExtension(pluginFile.getName());
		String pluginPath = pluginFile.getAbsolutePath().replace("\\", "/");

		Plugin plugin = new VST2Plugin(pluginName, pluginPath);

		File plist = new File(pluginFile.getAbsolutePath() + "/Contents/Info.plist");
		if (plist.exists()) {
			buildPluginFromVST2Plist(plugin, plist);
		}
		return plugin;
	}

	private Plugin buildVST3Plugin(File pluginFile) {
		String pluginName = FilenameUtils.removeExtension(pluginFile.getName());
		String pluginPath = pluginFile.getAbsolutePath().replace("\\", "/");

		return new VST3Plugin(pluginName, pluginPath);
	}

	private void buildPluginFromVST2Plist(Plugin plugin, File plist) {

		try {
			NSDictionary rootDict = (NSDictionary)PropertyListParser.parse(plist);
			NSObject nsBundleId = rootDict.objectForKey("CFBundleIdentifier");
			NSObject nsBundleVersion = rootDict.objectForKey("CFBun"
					+ "dleShortVersionString");

			if (nsBundleVersion == null) {
				nsBundleVersion = rootDict.objectForKey("CFBundleVersion");
			}
			if (nsBundleId != null) {
				plugin.setBundleId(nsBundleId.toString());
			}
			if (nsBundleVersion != null) {
				plugin.setVersion(nsBundleVersion.toString());
			}
		} catch (IOException | PropertyListFormatException | ParseException | ParserConfigurationException
				| SAXException e) {
			log.error("Error while building plugin from Plistfile", e);
		}	
	}
}
