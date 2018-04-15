package com.dropsnorz.owlplug;

import org.springframework.stereotype.Component;

import com.dropsnorz.owlplug.model.OSType;
import com.dropsnorz.owlplug.utils.OSValidator;

import javafx.scene.image.Image;

@Component
public class ApplicationDefaults {
	
	private OSType platform;
	
	public final static String REPOSITORY_FOLDER_NAME = "repositories";
	
	public final Image directoryImage = new Image(getClass().getResourceAsStream("/icons/folder-grey-16.png"));
	public final Image vst2Image  = new Image(getClass().getResourceAsStream("/icons/soundwave-blue-16.png"));
	
	
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

}
