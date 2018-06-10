package com.dropsnorz.owlplug.auth.services;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.auth.JPADataStoreFactory;
import com.dropsnorz.owlplug.auth.components.OwlPlugCredentials;
import com.dropsnorz.owlplug.auth.dao.GoogleCredentialDAO;
import com.dropsnorz.owlplug.auth.dao.UserAccountDAO;
import com.dropsnorz.owlplug.auth.model.UserAccountProvider;
import com.dropsnorz.owlplug.core.controllers.MainController;
import com.dropsnorz.owlplug.core.services.PluginRepositoryService;
import com.dropsnorz.owlplug.auth.model.UserAccount;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.Oauth2.Userinfo;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;

@Service
public class AuthentificationService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OwlPlugCredentials owlPlugCredentials;
	
	@Autowired
	private GoogleCredentialDAO googleCredentialDAO;
	
	@Autowired
	private UserAccountDAO userAccountDAO;
	
	@Autowired
	private MainController mainController;
	
	@Autowired
	private PluginRepositoryService pluginRepositoryService;
	
	@Autowired
	Preferences prefs;
	
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private LocalServerReceiver receiver = null;

	
	public void createAccountAndAuth() {
		
		String clientId = owlPlugCredentials.GOOGLE_APP_ID;
		String clientSecret = owlPlugCredentials.GOOGLE_SECRET;
		ArrayList<String>scopes = new ArrayList<String>();
		
		scopes.add("https://www.googleapis.com/auth/drive");
		scopes.add("https://www.googleapis.com/auth/userinfo.profile");
		
		try {
			NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			DataStoreFactory dataStore = new JPADataStoreFactory(googleCredentialDAO);
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientId, clientSecret, scopes)
					.setDataStoreFactory(dataStore)
					.setAccessType("offline").setApprovalPrompt("force")
					.build();
			
			UserAccount userAccount = new UserAccount();
			userAccountDAO.save(userAccount);
			
			receiver = new LocalServerReceiver();
		
			AuthorizationCodeInstalledApp authCodeAccess = new AuthorizationCodeInstalledApp(flow, receiver);
			Credential credential = authCodeAccess.authorize(userAccount.getKey());
						
			 Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(
			          "OwlPlug").build();
			 Userinfoplus userinfo = oauth2.userinfo().get().execute();
			 
			 userAccount.setName(userinfo.getName());
			 userAccount.setIconUrl(userinfo.getPicture());
			 userAccount.setAccountProvider(UserAccountProvider.GOOGLE);
			 userAccount.setCredential(googleCredentialDAO.findByKey(userAccount.getKey()));
			 
			 userAccountDAO.save(userAccount);
			 prefs.putLong(ApplicationDefaults.SELECTED_ACCOUNT_KEY, userAccount.getId());

		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public GoogleCredential getGoogleCredential(String key){
		
		String clientId = owlPlugCredentials.GOOGLE_APP_ID;
		String clientSecret = owlPlugCredentials.GOOGLE_SECRET;
		
		com.dropsnorz.owlplug.auth.model.GoogleCredential gc = googleCredentialDAO.findByKey(key);
		
		 return new GoogleCredential.Builder().setTransport(new NetHttpTransport())
	        .setJsonFactory(new JacksonFactory())
	        .setClientSecrets(clientId, clientSecret)
	        .build()
	        .setRefreshToken(gc.getRefreshToken());
	}
	
	@Transactional
	public void deleteAccount(UserAccount userAccount) {
		
		pluginRepositoryService.removeAccountReferences(userAccount);
		
		googleCredentialDAO.deleteByKey(userAccount.getKey());
		userAccountDAO.delete(userAccount);
		
		mainController.refreshAccounts();
	}
	
	
	public void stopAuthReceiver() {
		try {
			userAccountDAO.deleteInvalidAccounts();
			if (receiver!= null) receiver.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
