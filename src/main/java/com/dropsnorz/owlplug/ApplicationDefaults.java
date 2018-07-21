package com.dropsnorz.owlplug;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.dropsnorz.owlplug.core.model.OSType;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.utils.FileUtils;
import com.dropsnorz.owlplug.core.utils.OSValidator;

import javafx.scene.image.Image;

@Component
public class ApplicationDefaults {
	
	@Autowired
	private Environment env;

	private OSType platform;

	public static final String APPLICATION_NAME = "OwlPlug";
	public static final String REPOSITORY_FOLDER_NAME = "repositories";
	public static final String DEFAULT_REPOSITORY_DIRECTORY = "C:/VST";

	public static final Image owlplugLogo = new Image(ApplicationDefaults.class.getResourceAsStream("/media/owlplug-logo.png"));
	public final Image directoryImage = new Image(getClass().getResourceAsStream("/icons/folder-grey-16.png"));
	public final Image vst2Image  = new Image(getClass().getResourceAsStream("/icons/vst2-blue-16.png"));
	public final Image vst3Image  = new Image(getClass().getResourceAsStream("/icons/vst3-green-16.png"));
	public final Image repositoryImage  = new Image(getClass().getResourceAsStream("/icons/box-yellow-16.png"));
	public final Image taskPendingImage = new Image(getClass().getResourceAsStream("/icons/loading-grey-16.png"));
	public final Image taskSuccessImage = new Image(getClass().getResourceAsStream("/icons/check-green-16.png"));
	public final Image taskFailImage = new Image(getClass().getResourceAsStream("/icons/cross-red-16.png"));
	public final Image taskRunningImage = new Image(getClass().getResourceAsStream("/icons/play-green-16.png"));
	public final Image fileSystemRepositoryImage = new Image(getClass().getResourceAsStream("/icons/filesystem-grey-48.png"));
	public final Image googleDriveRepositoryImage = new Image(getClass().getResourceAsStream("/icons/gdrive-grey-64.png"));
	
	
	public final Image pluginPlaceholderImage = new Image(getClass().getResourceAsStream("/media/plugin-placeholder.png"));

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
	
	public String getVersion() {
		return env.getProperty("owlplug.version");
	}
	
	public String getUserDataDirectory() {
		return FileUtils.convertPath(System.getProperty("user.home") + File.separator + ".owlplug");
		
	}
	
	public String getTempDowloadDirectory() {
		return getUserDataDirectory() + "/temp";
	}
	
	public static final String VST_DIRECTORY_KEY = "VST_DIRECTORY";
	public static final String VST2_DISCOVERY_ENABLED_KEY = "VST2_DISCOVERY_ENABLED";
	public static final String VST3_DISCOVERY_ENABLED_KEY = "VST3_DISCOVERY_ENABLED";
	public static final String SELECTED_ACCOUNT_KEY = "SELECTED_ACCOUNT_KEY";
	public static final String SYNC_PLUGINS_STARTUP_KEY = "SYNC_PLUGINS_STARTUP_KEY";
	public static final String STORE_DIRECTORY_ENABLED_KEY = "STORE_DIRECTORY_ENABLED_KEY";
	public static final String STORE_DIRECTORY_KEY = "STORE_DIRECTORY_KEY";


}
