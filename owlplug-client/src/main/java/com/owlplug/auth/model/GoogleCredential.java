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

import com.google.api.client.auth.oauth2.StoredCredential;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Owlplug JPA entity to handle google credentials.
 *
 */
@Entity
public class GoogleCredential {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Column(unique = true)
  private String key;
  private String accessToken;
  private Long expirationTimeMilliseconds;
  private String refreshToken;

  @CreatedDate
  private Instant createdAt;
  @LastModifiedDate
  private Instant updatedAt;

  public GoogleCredential() {

  }

  /**
   * Creates a new GoogleCredential from the given {@link StoredCredential}
   * properties.
   * 
   * @param key        unique credential key
   * @param credential original credential
   */
  public GoogleCredential(String key, StoredCredential credential) {
    this.key = key;
    this.accessToken = credential.getAccessToken();
    this.expirationTimeMilliseconds = credential.getExpirationTimeMilliseconds();
    this.refreshToken = credential.getRefreshToken();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  /**
   * Apply properties of the given {@link StoredCredential }.
   * 
   * @param credential original credential
   */
  public void apply(StoredCredential credential) {
    this.accessToken = credential.getAccessToken();
    this.expirationTimeMilliseconds = credential.getExpirationTimeMilliseconds();
    this.refreshToken = credential.getRefreshToken();
    this.updatedAt = Instant.now();
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public Long getExpirationTimeMilliseconds() {
    return expirationTimeMilliseconds;
  }

  public void setExpirationTimeMilliseconds(Long expirationTimeMilliseconds) {
    this.expirationTimeMilliseconds = expirationTimeMilliseconds;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

}