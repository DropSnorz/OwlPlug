package com.dropsnorz.owlplug.auth.dao;

import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dropsnorz.owlplug.auth.model.GoogleCredential;

@Repository
public interface GoogleCredentialDAO extends CrudRepository<GoogleCredential, Long> {
    GoogleCredential findByKey(String key);
    GoogleCredential findByAccessToken(String key);
    
    @Query(value = "select key from GOOGLE_CREDENTIAL", nativeQuery = true)
    Set<String> findAllKeys();
    
	@Query("select c from GoogleCredential c")
	Stream<GoogleCredential> findAllCredentialAsStream();
	
	void deleteByKey(String key);
}