package com.dropsnorz.owlplug;

import org.springframework.stereotype.Component;

import com.dropsnorz.owlplug.model.OSType;
import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.utils.OSValidator;

import javafx.scene.image.Image;

@Component
public class ApplicationDefaults {

	private OSType platform;

	public final static String REPOSITORY_FOLDER_NAME = "repositories";

	public final Image directoryImage = new Image(getClass().getResourceAsStream("/icons/folder-grey-16.png"));
	public final Image vst2Image  = new Image(getClass().getResourceAsStream("/icons/vst2-blue-16.png"));
	public final Image vst3Image  = new Image(getClass().getResourceAsStream("/icons/vst3-green-16.png"));
	public final Image repositoryImage  = new Image(getClass().getResourceAsStream("/icons/box-yellow-16.png"));

	public ApplicationDefaults() {
		if(OSValidator.isWindows()) {
			this.platform = OSType.WIN;
		}
		else if(OSValidator.isMac()){
			this.platform = OSType.MAC;
		}
		else {
			this.platform = OSType.UNDEFINED;
		}
	}

	public OSType getPlatform() {

		return platform;
	}

	public Image getPluginIcon(Plugin plugin) {

		switch (plugin.getType()) {
		case VST2: return vst2Image;
		case VST3: return vst3Image;
		default: return vst2Image;
		}
	}
	
	public static String VST_DIRECTORY_KEY = "VST_DIRECTORY";
	public static String VST2_DISCOVERY_ENABLED_KEY = "VST2_DISCOVERY_ENABLED";
	public static String VST3_DISCOVERY_ENABLED_KEY = "VST3_DISCOVERY_ENABLED";

}
