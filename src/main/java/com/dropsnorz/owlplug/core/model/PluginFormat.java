package com.dropsnorz.owlplug.core.model;

public enum PluginFormat {
	VST2("VST2"),
	VST3("VST3");
	
	private String text;
	
	PluginFormat(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

}
