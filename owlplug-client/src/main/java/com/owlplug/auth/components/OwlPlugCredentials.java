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
 
package com.owlplug.auth.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Google OAuth secrets storage bean
 * Contains Google APÏ AppId and Secret filled on startup from config files.
 *
 */
@Component
@PropertySource("classpath:credentials.properties")
public class OwlPlugCredentials {

  @Value("${owlplug.credentials.google.appId}")
  private String googleAppId;

  @Value("${owlplug.credentials.google.secret}")
  private String googleSecret;

  public String getGoogleAppId() {
    return googleAppId;
  }

  public String getGoogleSecret() {
    return googleSecret;
  }

}
