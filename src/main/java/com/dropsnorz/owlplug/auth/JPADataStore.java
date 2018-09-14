package com.dropsnorz.owlplug.auth;

import com.dropsnorz.owlplug.auth.dao.GoogleCredentialDAO;
import com.dropsnorz.owlplug.auth.model.GoogleCredential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.DataStore;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Google JPA Datastore.
 * Persists {@link StoredCredential} using JPA providers 
 */
public class JPADataStore extends AbstractDataStore<StoredCredential> {

	private GoogleCredentialDAO repository;
	private JPADataStoreFactory jpaDataStoreFactory;

	/**
	 * Creates a new JPA Datastore.
	 * @param dataStoreFactory data store factory
	 * @param id               data store ID
	 */
	protected JPADataStore(JPADataStoreFactory dataStoreFactory, String id, GoogleCredentialDAO repository) {
		super(dataStoreFactory, id);
		this.repository = repository;
	}


	@Override
	public JPADataStoreFactory getDataStoreFactory() {
		return jpaDataStoreFactory;
	}

	@Override
	public int size() throws IOException {
		return (int) repository.count();
	}

	@Override
	public boolean isEmpty() throws IOException {
		return size() == 0;
	}

	@Override
	public boolean containsKey(String key) throws IOException {
		return repository.findByKey(key) != null;
	}

	@Override
	public boolean containsValue(StoredCredential value) throws IOException {
		return repository.findByAccessToken(value.getAccessToken()) != null;
	}

	@Override
	public Set<String> keySet() throws IOException {
		return repository.findAllKeys();
	}

	@Override
	public Collection<StoredCredential> values() throws IOException {

		return repository.findAllCredentialAsStream().map(c -> {
			StoredCredential credential = new StoredCredential();
			credential.setAccessToken(c.getAccessToken());
			credential.setRefreshToken(c.getRefreshToken());
			credential.setExpirationTimeMilliseconds(c.getExpirationTimeMilliseconds());
			return credential;
		}).collect(Collectors.toList());
	}

	@Override
	public StoredCredential get(String key) throws IOException {
		GoogleCredential googleCredential = repository.findByKey(key);
		if (googleCredential == null) {
			return null;
		}
		StoredCredential credential = new StoredCredential();
		credential.setAccessToken(googleCredential.getAccessToken());
		credential.setRefreshToken(googleCredential.getRefreshToken());
		credential.setExpirationTimeMilliseconds(googleCredential.getExpirationTimeMilliseconds());
		return credential;
	}

	@Override
	public DataStore<StoredCredential> set(String key, StoredCredential value) throws IOException {
		GoogleCredential googleCredential = repository.findByKey(key);

		if (googleCredential == null) {
			googleCredential = new GoogleCredential(key, value);
		}

		googleCredential.apply(value);
		repository.save(googleCredential);
		return this;
	}

	@Override
	public DataStore<StoredCredential> clear() throws IOException {
		repository.deleteAll();
		return this;
	}

	@Override
	public DataStore<StoredCredential> delete(String key) throws IOException {
		repository.deleteByKey(key);
		return this;
	}
}