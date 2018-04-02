package com.dropsnorz.owlplug.controllers.dialogs;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class DialogFrame {
	
	@Autowired
	DialogController dialogController;
	
	
	public void close() {
		
		dialogController.getDialog().close();
		
	}
	
	public void setOverlayClose(boolean overlayClose) {
		
		//dialogController.getDialog().setOverlayClose(overlayClose);
	}

}
