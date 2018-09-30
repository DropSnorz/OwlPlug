package com.dropsnorz.owlplug.core.model;

public enum PluginType {
	SYNTH("synth"),
	EFFECT("effect");

	private String text;

	PluginType(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	/**
	 * Retrieves an enum instance matching a text string. Returns null if the given string 
	 * doesn't match any defined enum instance.
	 * @param text enum unique text
	 * @return
	 */
	public static PluginType fromString(String text) {
		for (PluginType b : PluginType.values()) {
			if (b.text.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}

}
