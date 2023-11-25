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
 
package com.owlplug.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.owlplug.auth.dao.GoogleCredentialDAO;
import com.owlplug.auth.model.GoogleCredential;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class GoogleCredentialDAOTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private GoogleCredentialDAO googleCredentialDAO;

  @BeforeAll
  public void beforeTest() {
    GoogleCredential gc = new GoogleCredential();
    gc.setKey("TEST-KEY-1");
    entityManager.persist(gc);

    gc = new GoogleCredential();
    gc.setKey("TEST-KEY-2");
    entityManager.persist(gc);
    entityManager.flush();

  }

  @Test
  public void findGoogleCredentialKeySetTest() {
    Set<String> keys = googleCredentialDAO.findAllKeys();

    assertNotNull(keys);
    assertEquals(2, keys.size());
    assertThat(keys, containsInAnyOrder("TEST-KEY-1", "TEST-KEY-2"));

  }

  @Test
  public void findGoogleCredentialStreamTest() {

    Stream<GoogleCredential> stream = googleCredentialDAO.findAllCredentialAsStream();
    assertNotNull(stream);
    assertEquals(2, stream.count());

    stream = googleCredentialDAO.findAllCredentialAsStream();
    assertThat((Iterable<GoogleCredential>)stream::iterator, contains(
        hasProperty("key", is("TEST-KEY-1")), 
        hasProperty("key", is("TEST-KEY-2"))
        ));

  }

}
