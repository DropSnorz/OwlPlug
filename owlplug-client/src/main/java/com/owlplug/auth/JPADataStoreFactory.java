package com.owlplug.auth;

import com.google.api.client.util.store.DataStoreFactory;
import com.owlplug.auth.dao.GoogleCredentialDAO;
import java.io.IOException;

/**
 * JPADataStore factory. Creates new {@link JPADataStore} instances.
 *
 */
public class JPADataStoreFactory implements DataStoreFactory {

  private GoogleCredentialDAO repository;

  public JPADataStoreFactory(GoogleCredentialDAO repository) {
    this.repository = repository;
  }

  @Override
  public JPADataStore getDataStore(String id) throws IOException {
    return new JPADataStore(this, id, repository);
  }

}
