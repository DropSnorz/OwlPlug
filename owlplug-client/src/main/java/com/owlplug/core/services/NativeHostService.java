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
 
package com.owlplug.core.services;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.host.NativeHost;
import org.springframework.stereotype.Service;

@Service
public class NativeHostService extends BaseService {
  
  private NativeHost nativeHost = new NativeHost();
  
  public NativeHost getNativeHost() {
    return nativeHost;
  }
  
  public boolean isNativeHostEnabled() {
    return this.getPreferences().getBoolean(ApplicationDefaults.NATIVE_HOST_ENABLED_KEY, false)
        && nativeHost.isAvailable();
  }
  
  public boolean isNativeHostAvailable() {
    return nativeHost.isAvailable();
  }
  
  

}
