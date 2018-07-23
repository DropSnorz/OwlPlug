package com.dropsnorz.owlplug.core.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

@Service
public class OwlPlugCentralService {

	@Value("${owlplugcentral.url}")
	private String owlplugCentralUrl;


	public String getPluginImageUrl(String name) {

		return owlplugCentralUrl + "/image-registry?name=" +  UriUtils.encode(name, "UTF-8");

	}

}
