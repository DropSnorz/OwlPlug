package com.owlplug.core.services;

import com.owlplug.core.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OwlPlugCentralService {

  @Value("${owlplugcentral.url}")
  private String owlplugCentralUrl;

  public String getPluginImageUrl(String name) {

    return owlplugCentralUrl + "/image-registry?name=" + UrlUtils.encodeQuery(name);

  }

}
