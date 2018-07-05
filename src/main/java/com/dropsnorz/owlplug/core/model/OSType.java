package com.dropsnorz.owlplug.core.model;

public enum OSType {
	WIN("win"),
	MAC("osx"),
	UNIX("unix"),
	UNDEFINED("undefined");
	
	private String code;
	
	OSType(String code) {
		this.code = code;
	}
	
	
	public String getCode(){
		return code;
	}

}
