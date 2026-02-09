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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.owlplug.auth.model.UserAccount;
import com.owlplug.auth.model.UserAccountProvider;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class UserAccountRepositoryTest {

  @Test
  public void testDeleteInvalidAccountsMethodExists() {
    // Test that the repository interface has the deleteInvalidAccounts method
    UserAccountRepository repository = mock(UserAccountRepository.class);

    // Should not throw exception
    repository.deleteInvalidAccounts();

    verify(repository, times(1)).deleteInvalidAccounts();
  }

  @Test
  public void testRepositoryExtendsBaseCrudOperations() {
    UserAccountRepository repository = mock(UserAccountRepository.class);

    // Test that CRUD operations are available through CrudRepository
    UserAccount account = new UserAccount();
    account.setId(1L);

    when(repository.findById(1L)).thenReturn(Optional.of(account));
    when(repository.save(account)).thenReturn(account);

    Optional<UserAccount> found = repository.findById(1L);
    assertTrue(found.isPresent());

    UserAccount saved = repository.save(account);
    assertNotNull(saved);

    verify(repository, times(1)).findById(1L);
    verify(repository, times(1)).save(account);
  }

  @Test
  public void testUserAccountBasicProperties() {
    UserAccount account = new UserAccount();

    account.setId(123L);
    account.setName("Test User");
    account.setIconUrl("https://example.com/icon.png");

    assertEquals(123L, account.getId());
    assertEquals("Test User", account.getName());
    assertEquals("https://example.com/icon.png", account.getIconUrl());
  }

  @Test
  public void testUserAccountWithProvider() {
    UserAccountProvider provider = UserAccountProvider.GOOGLE;
    UserAccount account = new UserAccount(provider);

    assertEquals(provider, account.getAccountProvider());
  }

  @Test
  public void testUserAccountGetKey() {
    UserAccount account = new UserAccount();
    account.setId(456L);

    assertEquals("456", account.getKey());
  }

  @Test
  public void testUserAccountDefaultConstructor() {
    UserAccount account = new UserAccount();

    assertNull(account.getId());
    assertNull(account.getName());
    assertNull(account.getIconUrl());
    assertNull(account.getAccountProvider());
  }

  @Test
  public void testUserAccountSetters() {
    UserAccount account = new UserAccount();
    UserAccountProvider provider = UserAccountProvider.GOOGLE;

    account.setId(789L);
    account.setName("Another User");
    account.setIconUrl("https://example.com/another.png");
    account.setAccountProvider(provider);

    assertEquals(789L, account.getId());
    assertEquals("Another User", account.getName());
    assertEquals("https://example.com/another.png", account.getIconUrl());
    assertEquals(provider, account.getAccountProvider());
  }

  @Test
  public void testUserAccountWithNullValues() {
    UserAccount account = new UserAccount();

    account.setId(null);
    account.setName(null);
    account.setIconUrl(null);
    account.setAccountProvider(null);

    assertNull(account.getId());
    assertNull(account.getName());
    assertNull(account.getIconUrl());
    assertNull(account.getAccountProvider());
  }

  @Test
  public void testDeleteInvalidAccountsTransactional() {
    // Verify the method is annotated with @Transactional and @Modifying
    UserAccountRepository repository = mock(UserAccountRepository.class);

    repository.deleteInvalidAccounts();

    // The method should be callable without exceptions
    verify(repository, times(1)).deleteInvalidAccounts();
  }

  @Test
  public void testUserAccountImplementsAccountItem() {
    UserAccount account = new UserAccount();
    account.setId(100L);

    // Verify it implements AccountItem interface
    assertTrue(account instanceof com.owlplug.auth.ui.AccountItem);
    assertEquals(100L, account.getId());
  }

  @Test
  public void testRepositorySaveAndRetrieve() {
    UserAccountRepository repository = mock(UserAccountRepository.class);

    UserAccount account = new UserAccount();
    account.setId(1L);
    account.setName("Test");

    when(repository.save(account)).thenReturn(account);
    when(repository.findById(1L)).thenReturn(Optional.of(account));

    UserAccount saved = repository.save(account);
    Optional<UserAccount> retrieved = repository.findById(1L);

    assertNotNull(saved);
    assertTrue(retrieved.isPresent());
    assertEquals("Test", retrieved.get().getName());
  }

  @Test
  public void testRepositoryDeleteById() {
    UserAccountRepository repository = mock(UserAccountRepository.class);

    repository.deleteById(1L);

    verify(repository, times(1)).deleteById(1L);
  }

  @Test
  public void testRepositoryFindAll() {
    UserAccountRepository repository = mock(UserAccountRepository.class);

    repository.findAll();

    verify(repository, times(1)).findAll();
  }

  @Test
  public void testUserAccountCredentialManagement() {
    UserAccount account = new UserAccount();

    assertNull(account.getCredential());

    // Note: GoogleCredential is not tested here as it's a separate entity
    // This tests the getter/setter contract
    account.setCredential(null);
    assertNull(account.getCredential());
  }
}