/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.auth.repositories;

import com.owlplug.auth.model.GoogleCredential;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoogleCredentialRepository extends CrudRepository<GoogleCredential, Long> {

  GoogleCredential findByKey(String key);

  GoogleCredential findByAccessToken(String key);

  @Query(value = "select key from GOOGLE_CREDENTIAL", nativeQuery = true)
  Set<String> findAllKeys();

  @Query("select c from GoogleCredential c")
  Stream<GoogleCredential> findAllCredentialAsStream();

  void deleteByKey(String key);
}