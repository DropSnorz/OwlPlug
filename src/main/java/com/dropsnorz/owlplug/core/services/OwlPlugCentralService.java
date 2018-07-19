package com.dropsnorz.owlplug.core.services;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OwlPlugCentralService {
	
	@Value("${owlplugcentral.url}")
	private String owlplugCentralUrl;

	
	public String getPluginImageUrl(String name) {
		
		try {
			return owlplugCentralUrl + "/image-registry?name=" + URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		
	}

}
