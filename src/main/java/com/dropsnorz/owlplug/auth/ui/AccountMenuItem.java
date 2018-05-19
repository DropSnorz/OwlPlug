package com.dropsnorz.owlplug.auth.ui;

public class AccountMenuItem implements AccountItem{
	
	private String text;

	public AccountMenuItem(String text) {
		super();
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	

}
