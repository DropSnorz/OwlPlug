package com.dropsnorz.owlplug.auth;

import com.dropsnorz.owlplug.auth.dao.GoogleCredentialDAO;
import com.google.api.client.util.store.DataStoreFactory;
import java.io.IOException;

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
