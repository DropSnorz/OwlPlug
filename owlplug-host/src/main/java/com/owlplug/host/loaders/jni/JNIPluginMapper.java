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

package com.owlplug.host.loaders.jni;

import com.owlplug.host.NativePlugin;
import com.owlplug.host.io.ClassPathVersionUtils;
import com.owlplug.host.io.LibraryLoader;
import com.owlplug.host.utils.OSUtils;
import java.util.List;

public class JNIPluginMapper {

  private static final String LIB_NAME = "owlplug-host";
  private static final String LIB_VERSION = ClassPathVersionUtils.getVersionSafe(LIB_NAME);
  private static final String LIB_TAG = OSUtils.getPlatformTagName();
  private static final String LIB_ID = LIB_NAME + "-" + LIB_VERSION + '-' + LIB_TAG;

  private static JNIPluginMapper INSTANCE;
  private boolean isNativeLibraryLoaded;

  private JNIPluginMapper() {

  }

  public static JNIPluginMapper getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new JNIPluginMapper();
    }
    return INSTANCE;
  }

  public void init() {
    if (!isNativeLibraryLoaded()) {
      isNativeLibraryLoaded = LibraryLoader.load(LIB_ID, JNIPluginMapper.class);
    }
  }
  
  public boolean isNativeLibraryLoaded() {
    return isNativeLibraryLoaded;
  }
  
  public native List<NativePlugin> mapPlugin(String path);

}
