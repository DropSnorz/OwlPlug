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
 
package com.owlplug.auth.model;

import com.owlplug.auth.ui.AccountItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

/**
 * OwlPlug JPA Entity class to handle users accounts information.
 */
@Entity
public class UserAccount implements AccountItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;
  private String iconUrl;
  private UserAccountProvider accountProvider;
  @OneToOne(cascade = CascadeType.REMOVE, optional = true)
  private GoogleCredential credential;

  public UserAccount() {

  }

  public UserAccount(UserAccountProvider accountProvider) {
    this.accountProvider = accountProvider;

  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getKey() {
    return Long.toString(id);
  }

  public UserAccountProvider getAccountProvider() {
    return accountProvider;
  }

  public void setAccountProvider(UserAccountProvider accountProvider) {
    this.accountProvider = accountProvider;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }

  public GoogleCredential getCredential() {
    return credential;
  }

  public void setCredential(GoogleCredential credential) {
    this.credential = credential;
  }

}
