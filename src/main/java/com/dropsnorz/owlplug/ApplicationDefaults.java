package com.dropsnorz.owlplug;

import org.springframework.stereotype.Component;

import com.dropsnorz.owlplug.model.OSType;
import com.dropsnorz.owlplug.utils.OSValidator;

@Component
public class ApplicationDefaults {
	
	private OSType platform;
	
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
