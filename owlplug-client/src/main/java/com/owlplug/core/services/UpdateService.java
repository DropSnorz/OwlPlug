package com.owlplug.core.services;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.model.json.RemoteVersion;
import com.vdurmont.semver4j.Semver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class UpdateService {
  
  @Autowired
  ApplicationDefaults applicationDefaults;
  
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * Returns application update status based on OwlPlug Hub remote version.
   * @return true if app is up to date, false otherwise
   */
  public boolean isUpToDate() {
    
    String remoteVersion = getLastVersion();
    
    if (remoteVersion != null) {
      Semver remoteSemver = new Semver(remoteVersion);
      Semver currentSemver = new Semver(applicationDefaults.getVersion());
      return remoteSemver.isLowerThanOrEqualTo(currentSemver);

    }
    return true;

  }
  
  private String getLastVersion() {
    RestTemplate restTemplate = new RestTemplate();
    RemoteVersion remoteVersion = null;
    try {
      remoteVersion = restTemplate.getForObject(applicationDefaults.getOwlPlugHubUrl() 
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

