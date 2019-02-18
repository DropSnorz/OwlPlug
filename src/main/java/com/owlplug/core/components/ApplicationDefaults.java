package com.owlplug.core.components;

import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.platform.RuntimePlatform;
import com.owlplug.core.model.platform.RuntimePlatformResolver;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.store.model.StoreProduct;
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
  public static final String REPOSITORY_FOLDER_NAME = "repositories";
  public static final String DEFAULT_VST_DIRECTORY = "C:/VST";

  // CHECKSTYLE:OFF
  public static final Image owlplugLogo = new Image(
      ApplicationDefaults.class.getResourceAsStream("/media/owlplug-logo.png"));
  public final Image directoryImage = new Image(getClass().getResourceAsStream("/icons/folder-grey-16.png"));
  public final Image vst2Image = new Image(getClass().getResourceAsStream("/icons/vst2-blue-16.png"));
  public final Image vst3Image = new Image(getClass().getResourceAsStream("/icons/vst3-green-16.png"));
  public final Image repositoryImage = new Image(getClass().getResourceAsStream("/icons/box-yellow-16.png"));
  public final Image taskPendingImage = new Image(getClass().getResourceAsStream("/icons/loading-grey-16.png"));
  public final Image taskSuccessImage = new Image(getClass().getResourceAsStream("/icons/check-green-16.png"));
  public final Image taskFailImage = new Image(getClass().getResourceAsStream("/icons/cross-red-16.png"));
  public final Image taskRunningImage = new Image(getClass().getResourceAsStream("/icons/play-green-16.png"));
  public final Image fileSystemRepositoryImage = new Image(
      getClass().getResourceAsStream("/icons/filesystem-grey-48.png"));
  public final Image googleDriveRepositoryImage = new Image(
      getClass().getResourceAsStream("/icons/gdrive-grey-64.png"));
  public final Image rocketImage = new Image(getClass().getResourceAsStream("/icons/rocket-white-64.png"));
  public final Image storeImage = new Image(getClass().getResourceAsStream("/icons/bag-white-32.png"));
  public final Image instrumentImage = new Image(getClass().getResourceAsStream("/icons/synth-white-16.png"));
  public final Image effectImage = new Image(getClass().getResourceAsStream("/icons/effect-white-16.png"));
  public final Image tagImage = new Image(getClass().getResourceAsStream("/icons/tag-white-16.png"));

  public final Image pluginPlaceholderImage = new Image(
      getClass().getResourceAsStream("/media/plugin-placeholder.png"));
  // CHECKSTYLE:ON

  public static final String VST_DIRECTORY_KEY = "VST_DIRECTORY";
  public static final String VST2_DISCOVERY_ENABLED_KEY = "VST2_DISCOVERY_ENABLED";
  public static final String VST3_DIRECTORY_KEY = "VST3_DIRECTORY";
  public static final String VST3_DISCOVERY_ENABLED_KEY = "VST3_DISCOVERY_ENABLED";
  public static final String SELECTED_ACCOUNT_KEY = "SELECTED_ACCOUNT_KEY";
  public static final String SYNC_PLUGINS_STARTUP_KEY = "SYNC_PLUGINS_STARTUP_KEY";
  public static final String STORE_DIRECTORY_ENABLED_KEY = "STORE_DIRECTORY_ENABLED_KEY";
  public static final String STORE_DIRECTORY_KEY = "STORE_DIRECTORY_KEY";
  public static final String STORE_SUBDIRECTORY_ENABLED = "STORE_SUBDIRECTORY_ENABLED";
  public static final String FIRST_LAUNCH_KEY = "FIRST_LAUNCH_KEY";

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
    default:
      return vst2Image;
    }
  }

  /**
   * Returns plugin icon based on plugin format.
   * 
   * @param product - product
   * @return Associated icon
   */
  public Image getProductTypeIcon(StoreProduct product) {

    switch (product.getType()) {
    case INSTRUMENT:
      return instrumentImage;
    case EFFECT:
      return effectImage;
    default:
      return null;
    }
  }

  public String getVersion() {
    return env.getProperty("owlplug.version");
  }

  public static String getUserDataDirectory() {
    return FileUtils.convertPath(System.getProperty("user.home") + File.separator + ".owlplug");

  }

  public static String getTempDowloadDirectory() {
    return getUserDataDirectory() + "/temp";
  }

}
