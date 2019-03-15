package com.owlplug.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.owlplug.auth.dao.GoogleCredentialDAO;
import com.owlplug.auth.model.GoogleCredential;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GoogleCredentialDAOTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private GoogleCredentialDAO googleCredentialDAO;

  @Before
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
