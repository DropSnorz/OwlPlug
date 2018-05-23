package com.dropsnorz.owlplug.core.controllers.dialogs;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.Node;

public abstract class AbstractDialog {
	
	@Autowired
	DialogController dialogController;
	
	
	protected abstract Node getNode();
	
	
	public void show() {
		
		onDialogShow();
		dialogController.newDialog(this.getNode());
		dialogController.getDialog().show();
		
	}
		
	
	public void close() {
		
		onDialogClose();
		dialogController.getDialog().close();
		
	}
	
	protected void setOverlayClose(boolean overlayClose) {
		
		dialogController.getDialog().setOverlayClose(overlayClose);
	}
	
	protected void onDialogShow() {
		
	}
	
	protected void onDialogClose() {
		
	}
	
	

}
