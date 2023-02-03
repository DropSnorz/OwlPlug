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

package com.owlplug.core.components;

import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.model.platform.OperatingSystem;
import com.owlplug.core.model.platform.RuntimePlatform;
import com.owlplug.core.model.platform.RuntimePlatformResolver;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.explore.model.RemotePackage;
import java.io.File;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationDefaults {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private Environment env;

  private RuntimePlatform runtimePlatform;

  public static final String APPLICATION_NAME = "OwlPlug";

  // CHECKSTYLE:OFF
  public static final Image owlplugLogo = new Image(
      ApplicationDefaults.class.getResourceAsStream("/media/owlplug-logo.png"));

  public final Image owlplugLogoSmall = new Image(
      ApplicationDefaults.class.getResourceAsStream("/media/owlplug-logo-16.png"));
  public final Image directoryImage = new Image(getClass().getResourceAsStream("/icons/folder-grey-16.png"));
  public final Image vst2Image = new Image(getClass().getResourceAsStream("/icons/vst2-blue-16.png"));
  public final Image vst3Image = new Image(getClass().getResourceAsStream("/icons/vst3-green-16.png"));
  public final Image auImage = new Image(getClass().getResourceAsStream("/icons/au-purple-16.png"));
  public final Image lv2Image = new Image(getClass().getResourceAsStream("/icons/lv2-orange-16.png"));
  public final Image pluginComponentImage = new Image(getClass().getResourceAsStream("/icons/cube-white-16.png"));
  public final Image taskPendingImage = new Image(getClass().getResourceAsStream("/icons/loading-grey-16.png"));
  public final Image taskSuccessImage = new Image(getClass().getResourceAsStream("/icons/check-green-16.png"));
  public final Image taskFailImage = new Image(getClass().getResourceAsStream("/icons/cross-red-16.png"));
  public final Image taskRunningImage = new Image(getClass().getResourceAsStream("/icons/play-green-16.png"));
  public final Image rocketImage = new Image(getClass().getResourceAsStream("/icons/rocket-white-64.png"));
  public final Image serverImage = new Image(getClass().getResourceAsStream("/icons/server-white-32.png"));
  public final Image instrumentImage = new Image(getClass().getResourceAsStream("/icons/synth-white-16.png"));
  public final Image effectImage = new Image(getClass().getResourceAsStream("/icons/effect-white-16.png"));
  public final Image tagImage = new Image(getClass().getResourceAsStream("/icons/tag-white-16.png"));
  public final Image symlinkImage = new Image(getClass().getResourceAsStream("/icons/folderlink-grey-16.png"));
  public final Image userImage = new Image(getClass().getResourceAsStream("/icons/user-white-32.png"));
  public final Image rootDirectoryImage = new Image(getClass().getResourceAsStream("/icons/foldersearch-grey-16.png"));
  public final Image verifiedSourceImage = new Image(getClass().getResourceAsStream("/icons/doublecheck-grey-16.png"));
  public final Image suggestedSourceImage = new Image(
      ApplicationDefaults.class.getResourceAsStream("/icons/check-grey-16.png"));
  public final Image studiorackLogoSmall = new Image(
      ApplicationDefaults.class.getResourceAsStream("/media/studiorack-logo-16.png"));


  public final Image pluginPlaceholderImage = new Image(
      getClass().getResourceAsStream("/media/plugin-placeholder.png"));
  // CHECKSTYLE:ON

  public static final String VST_DIRECTORY_KEY = "VST_DIRECTORY";
  public static final String VST2_DISCOVERY_ENABLED_KEY = "VST2_DISCOVERY_ENABLED";
  public static final String VST2_EXTRA_DIRECTORY_KEY = "VST2_EXTRA_DIRECTORY_KEY";
  public static final String VST3_DIRECTORY_KEY = "VST3_DIRECTORY";
  public static final String VST3_DISCOVERY_ENABLED_KEY = "VST3_DISCOVERY_ENABLED";
  public static final String VST3_EXTRA_DIRECTORY_KEY = "VST3_EXTRA_DIRECTORY_KEY";
  public static final String AU_DIRECTORY_KEY = "AU_DIRECTORY_KEY";
  public static final String AU_DISCOVERY_ENABLED_KEY = "AU_DISCOVERY_ENABLED_KEY";
  public static final String AU_EXTRA_DIRECTORY_KEY = "AU_EXTRA_DIRECTORY_KEY";
  public static final String LV2_DIRECTORY_KEY = "LV2_DIRECTORY_KEY";
  public static final String LV2_DISCOVERY_ENABLED_KEY = "LV2_DISCOVERY_ENABLED_KEY";
  public static final String LV2_EXTRA_DIRECTORY_KEY = "LV2_EXTRA_DIRECTORY_KEY";
  public static final String NATIVE_HOST_ENABLED_KEY = "NATIVE_HOST_ENABLED_KEY";
  public static final String PREFERRED_NATIVE_LOADER = "PREFERRED_NATIVE_LOADER";
  public static final String SELECTED_ACCOUNT_KEY = "SELECTED_ACCOUNT_KEY";
  public static final String SYNC_PLUGINS_STARTUP_KEY = "SYNC_PLUGINS_STARTUP_KEY";
  public static final String STORE_DIRECTORY_ENABLED_KEY = "STORE_DIRECTORY_ENABLED_KEY";
  public static final String STORE_BY_CREATOR_ENABLED_KEY = "STORE_BY_CREATOR_ENABLED_KEY";
  public static final String STORE_DIRECTORY_KEY = "STORE_DIRECTORY_KEY";
  public static final String STORE_SUBDIRECTORY_ENABLED = "STORE_SUBDIRECTORY_ENABLED";
  public static final String FIRST_LAUNCH_KEY = "FIRST_LAUNCH_KEY";
  public static final String APPLICATION_STATE_KEY = "APPLICATION_STATE_KEY";
  public static final String SHOW_DIALOG_DISABLE_PLUGIN_KEY = "SHOW_DIALOG_DISABLE_PLUGIN_KEY";

  /**
   * Creates a new ApplicationDefaults.
   */
  public ApplicationDefaults() {

    RuntimePlatformResolver platformResolver = new RuntimePlatformResolver();
    runtimePlatform = platformResolver.resolve();
    log.info("Runtime Platform Resolved: " + runtimePlatform.toString());

  }

  public RuntimePlatform getRuntimePlatform() {
    return runtimePlatform;
  }

  /**
   * Returns plugin icon based on plugin format.
   * 
   * @param plugin - plugin
   * @return Associated icon
   */
  public Image getPluginFormatIcon(Plugin plugin) {

    switch (plugin.getFormat()) {
    case VST2:
      return vst2Image;
    case VST3:
      return vst3Image;
    case AU:
      return auImage;
    case LV2:
      return lv2Image;
    default:
      return vst2Image;
    }
  }

  /**
   * Returns plugin icon based on plugin format.
   * 
   * @param remotePackage - package
   * @return Associated icon
   */
  public Image getPackageTypeIcon(RemotePackage remotePackage) {

    switch (remotePackage.getType()) {
    case INSTRUMENT:
      return instrumentImage;
    case EFFECT:
      return effectImage;
    default:
      return null;
    }
  }

  public String getDefaultPluginPath(PluginFormat format) {

    if (runtimePlatform.getOperatingSystem().equals(OperatingSystem.WIN)) {
      if (format.equals(PluginFormat.VST2)) {
        return "C:/Program Files/VSTPlugins";
      } else if (format.equals(PluginFormat.VST3)) {
        return "C:/Program Files/Common Files/VST3";
      } else if (format.equals(PluginFormat.LV2)) {
        return "C:/Program Files/Common Files/LV2";
      }
    } else if (runtimePlatform.getOperatingSystem().equals(OperatingSystem.MAC)) {
      if (format.equals(PluginFormat.VST2)) {
        return "/Library/Audio/Plug-ins/VST";
      } else if (format.equals(PluginFormat.VST3)) {
        return "/Library/Audio/Plug-ins/VST3";
      } else if (format.equals(PluginFormat.AU)) {
        return "/Library/Audio/Plug-ins/Components";
      } else if (format.equals(PluginFormat.LV2)) {
        return "/Library/Audio/Plug-Ins/LV2";
      }
    } else if (runtimePlatform.getOperatingSystem().equals(OperatingSystem.LINUX)) {
      if (format.equals(PluginFormat.VST2)) {
        return "/usr/lib/vst";
      } else if (format.equals(PluginFormat.VST3)) {
        return "/usr/lib/vst3";
      } else if (format.equals(PluginFormat.LV2)) {
        return "/usr/lib/lv2";
      }
    }

    return "/path/to/audio/plugins";
  }

  public String getVersion() {
    return env.getProperty("owlplug.version");
  }

  public String getOwlPlugHubUrl() {
    return env.getProperty("owlplug.hub.url");
  }

  public String getUpdateDownloadUrl() {
    return env.getProperty("owlplug.hub.updateDownloadUrl");
  }

  public String getOwlPlugRegistryUrl() {
    return env.getProperty("owlplug.registry.url");
  }

  public String getStudiorackRegistryUrl() {
    return env.getProperty("studiorack.registry.url");
  }
  public String getEnvProperty(String property) {
    return env.getProperty(property);
  }

  public static String getUserDataDirectory() {
    return FileUtils.convertPath(System.getProperty("user.home") + File.separator + ".owlplug");

  }

  public static String getTempDownloadDirectory() {
    return getUserDataDirectory() + "/temp";
  }

}
