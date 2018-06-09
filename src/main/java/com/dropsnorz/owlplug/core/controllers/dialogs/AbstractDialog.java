package com.dropsnorz.owlplug.core.controllers.dialogs;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.Node;

public abstract class AbstractDialog {
	
	@Autowired
	DialogController dialogController;
	
	private double sizeX = -1;
	private double sizeY = -1;
	
	public AbstractDialog() {
		
	}
	
	public AbstractDialog(double sizeX, double sizeY) {
		
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	
	protected abstract Node getNode();
	
	
	public void show() {
		
		onDialogShow();
		
		if(sizeX != -1 && sizeY != -1) {
			dialogController.newDialog(sizeX, sizeY, this.getNode());
		}
		else {
			dialogController.newDialog(this.getNode());
		}
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
