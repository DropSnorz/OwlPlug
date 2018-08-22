package com.dropsnorz.owlplug.core.controllers.dialogs;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDialogController {
	
	@Autowired
	private DialogController dialogController;
	
	private double width = -1;
	private double height = -1;
	private boolean overlayClose = true;
	
	public AbstractDialogController() {
		
	}
	
	/**
	 * Creates a new Dialog with fixed size.
	 * @param width dialog width
	 * @param height dialog height
	 */
	public AbstractDialogController(double width, double height) {
		
		this.width = width;
		this.height = height;
	}
	
	
	protected abstract Node getBody();
	
	protected abstract Node getHeading();
	
	
	/**
	 * Open and display dialog frame.
	 */
	public void show() {
		
		onDialogShow();
		
		if (width != -1 && height != -1) {
			if (this.getHeading() != null) {
				dialogController.newDialog(width, height, this.getBody(), this.getHeading());
			} else {
				dialogController.newDialog(width, height, this.getBody());
			}
		} else {
			if (this.getHeading() != null) {
				dialogController.newDialog(this.getBody(), this.getHeading());
			} else {
				dialogController.newDialog(this.getBody());
			}
		}
		dialogController.getDialog().setOverlayClose(overlayClose);
		dialogController.getDialog().show();

	}
		
	/**
	 * Close dialog frame.
	 */
	public void close() {
		onDialogClose();
		dialogController.getDialog().close();
		
	}
	
	protected void setOverlayClose(boolean overlayClose) {
		this.overlayClose = overlayClose;
	}
	
	protected void onDialogShow() {

	}
	
	protected void onDialogClose() {

	}
	
	
	
}
