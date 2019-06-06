/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.auth.services;

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
import com.google.api.services.oauth2.model.Userinfoplus;
import com.owlplug.auth.JPADataStoreFactory;
import com.owlplug.auth.components.OwlPlugCredentials;
import com.owlplug.auth.dao.GoogleCredentialDAO;
import com.owlplug.auth.dao.UserAccountDAO;
import com.owlplug.auth.model.UserAccount;
import com.owlplug.auth.model.UserAccountProvider;
import com.owlplug.auth.utils.AuthentificationException;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.controllers.MainController;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.prefs.Preferences;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OAuth Authentication service. Authenticates users from known providers using
 * IP Loopback and requests API call permissions for OwlPlug. This service
 * stores users access tokens for next calls. Only one Authentication flow must
 * be performed at time as this class is not thread safe and maintain states
 * during Authentication.
 *
 */
@Service
public class AuthenticationService {

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
  private Preferences prefs;

  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private LocalServerReceiver receiver = null;

  /**
   * Creates a new account by starting the Authentication flow.
   * 
   * @throws AuthentificationException if an error occurs during Authentication
   *                                   flow.
   */
  public void createAccountAndAuth() throws AuthentificationException {

    String clientId = owlPlugCredentials.getGoogleAppId();
    String clientSecret = owlPlugCredentials.getGoogleSecret();
    ArrayList<String> scopes = new ArrayList<>();

    scopes.add("https://www.googleapis.com/auth/drive");
    scopes.add("https://www.googleapis.com/auth/userinfo.profile");

    try {
      NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      DataStoreFactory dataStore = new JPADataStoreFactory(googleCredentialDAO);
      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientId,
          clientSecret, scopes).setDataStoreFactory(dataStore).setAccessType("offline").setApprovalPrompt("force")
              .build();

      UserAccount userAccount = new UserAccount();
      userAccountDAO.save(userAccount);

      receiver = new LocalServerReceiver();

      AuthorizationCodeInstalledApp authCodeAccess = new AuthorizationCodeInstalledApp(flow, receiver);
      Credential credential = authCodeAccess.authorize(userAccount.getKey());

      Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
          .setApplicationName("OwlPlug").build();
      Userinfoplus userinfo = oauth2.userinfo().get().execute();

      userAccount.setName(userinfo.getName());
      userAccount.setIconUrl(userinfo.getPicture());
      userAccount.setAccountProvider(UserAccountProvider.GOOGLE);
      userAccount.setCredential(googleCredentialDAO.findByKey(userAccount.getKey()));

      userAccountDAO.save(userAccount);
      prefs.putLong(ApplicationDefaults.SELECTED_ACCOUNT_KEY, userAccount.getId());

    } catch (GeneralSecurityException | IOException e) {
      log.error("Error during authentification", e);
      throw new AuthentificationException(e);
    } finally {
      // Delete accounts without complete setup
      userAccountDAO.deleteInvalidAccounts();
    }

  }

  /**
   * Retrieve Google Credentials from key.
   * 
   * @param key Google credential unique key
   * @return
   */
  public GoogleCredential getGoogleCredential(String key) {

    String clientId = owlPlugCredentials.getGoogleAppId();
    String clientSecret = owlPlugCredentials.getGoogleSecret();

    com.owlplug.auth.model.GoogleCredential gc = googleCredentialDAO.findByKey(key);

    return new GoogleCredential.Builder().setTransport(new NetHttpTransport()).setJsonFactory(new JacksonFactory())
        .setClientSecrets(clientId, clientSecret).build().setRefreshToken(gc.getRefreshToken());
  }

  /**
   * Deletes a user account.
   * 
   * @param userAccount user account to delete
   */
  @Transactional
  public void deleteAccount(UserAccount userAccount) {

    googleCredentialDAO.deleteByKey(userAccount.getKey());
    userAccountDAO.delete(userAccount);
    mainController.refreshAccounts();
  }
  
  /**
   * Returns all registered UserAccounts
   * @return list of user accounts
   */
  public Iterable<UserAccount> getAccounts() {
    return userAccountDAO.findAll();
  }
  
  /**
   * Returns a user account matching a given id.
   * @param id - user account unique id
   * @return related user account
   */
  public Optional<UserAccount> getUserAccountById(Long id) {
    return userAccountDAO.findById(id);
  }

  /**
   * Force the Authentication flow authReceiver to stop. Called when the user
   * cancels authentication.
   */
  public void stopAuthReceiver() {
    try {
      userAccountDAO.deleteInvalidAccounts();
      if (receiver != null) {
        receiver.stop();
      }
    } catch (IOException e) {
      log.error("Error while stopping local authentification receiver", e);
    }
  }

}
