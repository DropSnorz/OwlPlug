package com.dropsnorz.owlplug.auth.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:credentials.properties")
public class OwlPlugCredentials {

	@Value("${owlplug.credentials.google.appId}")
	public String GOOGLE_APP_ID;

	@Value("${owlplug.credentials.google.secret}")
	public String GOOGLE_SECRET;




}
