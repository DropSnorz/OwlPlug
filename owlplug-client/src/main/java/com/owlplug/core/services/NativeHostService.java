package com.owlplug.core.services;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.host.NativeHost;
import java.util.prefs.Preferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NativeHostService {
  
  @Autowired
  private Preferences prefs;
  
  private NativeHost nativeHost = new NativeHost();
  
  public NativeHost getNativeHost() {
    return nativeHost;
  }
  public boolean isNativeHostEnabled() {
    return prefs.getBoolean(ApplicationDefaults.NATIVE_HOST_ENABLED_KEY, false)
        && nativeHost.isAvailable();
  }
  
  public boolean isNativeHostAvailable() {
    return nativeHost.isAvailable();
  }
  
  

}
