package com.dropsnorz.owlplug.auth.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Google OAuth secrets storage bean
 * Contains Google APÏ AppId and Secret filled on startup from config files.
 *
 */
@Component
@PropertySource("classpath:credentials.properties")
public class OwlPlugCredentials {

	@Value("${owlplug.credentials.google.appId}")
	private String googleAppId;

	@Value("${owlplug.credentials.google.secret}")
	private String googleSecret;

	public String getGoogleAppId() {
		return googleAppId;
	}

	public String getGoogleSecret() {
		return googleSecret;
	}
	
}
