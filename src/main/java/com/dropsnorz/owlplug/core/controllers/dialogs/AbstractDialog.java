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
	
	
	protected abstract Node getBody();
	
	
	public void show() {
		
		onDialogShow();
		
		if(sizeX != -1 && sizeY != -1) {
			if(this.getHeading() != null) {
				dialogController.newDialog(sizeX, sizeY, this.getBody(), this.getHeading());
			}
			else {
				dialogController.newDialog(sizeX, sizeY, this.getBody());
			}
		}
		else {
			dialogController.newDialog(this.getBody());
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
	
	protected Node getHeading() {
		return null;
	}
	
	

}
