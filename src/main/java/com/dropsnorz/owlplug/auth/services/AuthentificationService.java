package com.dropsnorz.owlplug.auth.services;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.auth.components.OwlPlugCredentials;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;

@Service
public class AuthentificationService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OwlPlugCredentials owlPlugCredentials;
	
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final String  APPLICATION_NAME = "OwlPlug";
	private static Plus plus;

	
	public void startAuth() {
		
		String clientId = owlPlugCredentials.GOOGLE_APP_ID;
		String clientSecret = owlPlugCredentials.GOOGLE_SECRET;
		ArrayList<String>scopes = new ArrayList<String>();
		
		scopes.add("https://www.googleapis.com/auth/drive");
		
		
		try {
			NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			//dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			// authorization
			
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientId, clientSecret, scopes).build();
			// authorize
			Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
			
			plus = new Plus.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
			        APPLICATION_NAME).build();
			

		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
