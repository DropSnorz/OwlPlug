/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.core.services;

import com.owlplug.core.model.json.RemoteVersion;
import com.vdurmont.semver4j.Semver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class UpdateService extends BaseService {

  
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * Returns application update status based on OwlPlug Hub remote version.
   * @return true if app is up to date, false otherwise
   */
  public boolean isUpToDate() {
    
    String remoteVersion = getLastVersion();
    
    if (remoteVersion != null) {
      Semver remoteSemver = new Semver(remoteVersion);
      Semver currentSemver = new Semver(this.getApplicationDefaults().getVersion());
      return remoteSemver.isLowerThanOrEqualTo(currentSemver);

    }
    return true;

  }
  
  private String getLastVersion() {
    RestTemplate restTemplate = new RestTemplate();
    RemoteVersion remoteVersion = null;
    try {
      remoteVersion = restTemplate.getForObject(this.getApplicationDefaults().getOwlPlugHubUrl() 
          + "/releases/latest/version.json", RemoteVersion.class);
    } catch (RestClientException e) {
      log.error("Error retrieving latest owlplug version", e);
    }

    if (remoteVersion != null) {
      return remoteVersion.version;

    }
    return null;
    
  }
 
}

