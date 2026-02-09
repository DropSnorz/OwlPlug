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

import com.owlplug.core.model.OperatingSystem;
import com.owlplug.core.model.RuntimePlatform;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.project.model.DawApplication;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

  @Autowired
  private RuntimePlatformResolver runtimePlatformResolver;

  private static List<String> contributors;

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
  public final Image scanDirectoryImage = new Image(getClass().getResourceAsStream("/icons/foldersearch-grey-16.png"));
  public final Image verifiedSourceImage = new Image(getClass().getResourceAsStream("/icons/doublecheck-grey-16.png"));
  public final Image suggestedSourceImage = new Image(
      ApplicationDefaults.class.getResourceAsStream("/icons/check-grey-16.png"));
  public final Image openAudioLogoSmall = new Image(ApplicationDefaults.class.getResourceAsStream("/media/open-audio-16.png"));
  public final Image abletonLogoImage = new Image(getClass().getResourceAsStream("/icons/ableton-white-16.png"));
  public final Image reaperLogoImage = new Image(getClass().getResourceAsStream("/icons/reaper-white-16.png"));

  public final Image studioOneLogoImage = new Image(getClass().getResourceAsStream("/icons/studioone-white-16.png"));

  public final Image errorIconImage = new Image(
          getClass().getResourceAsStream("/icons/error-red-16.png"));

  public final Image linkIconImage = new Image(
          getClass().getResourceAsStream("/icons/link-grey-16.png"));
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
  public static final String PROJECT_DIRECTORY_KEY = "PROJECT_DIRECTORY_KEY";
  public static final String PLUGIN_PREFERRED_DISPLAY_KEY = "PLUGIN_PREFERRED_DISPLAY_KEY";
  public static final String SYNC_FILE_STAT_KEY = "SYNC_FILE_STAT_KEY";
  public static final String TELEMETRY_ENABLED_KEY = "TELEMETRY_ENABLED_KEY";
  public static final String TELEMETRY_USER_ID_KEY = "TELEMETRY_USER_ID_KEY";

  /**
   * Creates a new ApplicationDefaults.
   */
  public ApplicationDefaults() {

  }

  public RuntimePlatform getRuntimePlatform() {
    return runtimePlatformResolver.getCurrentPlatform();
  }

  /**
   * Returns plugin icon based on plugin format.
   * 
   * @param format - plugin format
   * @return Associated icon
   */
  public Image getPluginFormatIcon(PluginFormat format) {

    return switch (format) {
      case VST2 -> vst2Image;
      case VST3 -> vst3Image;
      case AU -> auImage;
      case LV2 -> lv2Image;
      default -> vst2Image;
    };
  }


  /**
   * Returns plugin icon based on plugin format.
   * 
   * @param remotePackage - package
   * @return Associated icon
   */
  public Image getPackageTypeIcon(RemotePackage remotePackage) {

    return switch (remotePackage.getType()) {
      case INSTRUMENT -> instrumentImage;
      case EFFECT -> effectImage;
      default -> null;
    };
  }

  public Image getDAWApplicationIcon(DawApplication application) {
    return switch (application) {
      case ABLETON -> abletonLogoImage;
      case REAPER -> reaperLogoImage;
      case STUDIO_ONE -> studioOneLogoImage;
    };
  }

  public String getDefaultPluginPath(PluginFormat format) {

    RuntimePlatform runtimePlatform = runtimePlatformResolver.getCurrentPlatform();

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

  public String getLatestUrl() {
    return env.getProperty("owlplug.github.latestUrl");
  }

  public String getDownloadUrl() {
    return env.getProperty("owlplug.github.downloadUrl");
  }

  public String getOwlPlugRegistryUrl() {
    return env.getProperty("owlplug.registry.url");
  }

  public String getOpenAudioRegistryUrl() {
    return env.getProperty("openaudio.registry.url");
  }


  public String getEnvProperty(String property) {
    return env.getProperty(property);
  }

  /**
   * Returns OwlPlug contributors list.
   * This method is static as it must be called in the Preloader
   * before controller been initialization.
   * @return list of contributors
   */
  public static List<String> getContributors() {

    if (contributors != null) {
      return new ArrayList<>(contributors);
    }
    String path = "/included/CONTRIBUTORS";
    InputStream input = ApplicationDefaults.class.getResourceAsStream(path);
    if (input == null) {
      throw new RuntimeException("Resource not found: " + path);
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
      contributors = reader.lines()
                 .filter(line -> !line.trim().isEmpty())
                 .collect(Collectors.toList());
      return new ArrayList<>(contributors);
    } catch (Exception e) {
      throw new RuntimeException("Error reading resource file: " + path, e);
    }
  }

  public static String getUserDataDirectory() {
    return Paths.get(System.getProperty("user.home"), ".owlplug").toString();

  }

  public static String getTempDownloadDirectory() {
    return Paths.get(getUserDataDirectory(), "temp").toString();
  }

  public static String getLogDirectory() {
    return Paths.get(getUserDataDirectory(), "logs").toString();
  }

}
