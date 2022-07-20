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

package com.owlplug.host.loaders;

import com.owlplug.host.model.OS;
import com.owlplug.host.utils.FileSystemUtils;
import com.owlplug.host.JuceXMLPlugin;
import com.owlplug.host.NativePlugin;
import com.owlplug.host.io.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EmbeddedScannerPluginLoader implements NativePluginLoader {

  private static final Logger log = LoggerFactory.getLogger(LibraryLoader.class);

  private static EmbeddedScannerPluginLoader INSTANCE;
  private static String SEPARATOR = System.getProperty("file.separator");

  private static final String DEFAULT_SCANNER_NAME = "owlplug-scanner";
  private static final String DEFAULT_SCANNER_VERSION = ClassPathVersionUtils.getVersionSafe(DEFAULT_SCANNER_NAME);
  private static String DEFAULT_SCANNER_EXT = getPlatformExecutableExtension();
  private static String DEFAULT_SCANNER_PLATFORM_TAG = getPlatformTagName();
  private static final String DEFAULT_SCANNER_ID =
      DEFAULT_SCANNER_NAME + "-" + DEFAULT_SCANNER_VERSION + "-" + DEFAULT_SCANNER_PLATFORM_TAG + DEFAULT_SCANNER_EXT;

  private boolean available = false;
  private String scannerDirectory;
  private String scannerId;

  public static EmbeddedScannerPluginLoader getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new EmbeddedScannerPluginLoader();
    }
    return INSTANCE;
  }

  private EmbeddedScannerPluginLoader() {
    scannerDirectory = System.getProperty("java.io.tmpdir");
    scannerId = DEFAULT_SCANNER_ID;
  }

  public EmbeddedScannerPluginLoader usingScannerPath(String path) {
    this.scannerDirectory = path;
    return this;
  }

  public EmbeddedScannerPluginLoader usingScannerId(String scannerId) {
    this.scannerId = scannerId;
    return this;
  }

  @Override
  public void init() {

    log.debug("Init plugin loader");
    File scannerFile = new File(scannerDirectory, scannerId);
    if (!scannerFile.exists()) {
      try {
        ClassPathFileExtractor.extract(this.getClass(), DEFAULT_SCANNER_ID, scannerFile);
      } catch (IOException e) {
        log.error("Scanner executable can't be extracted to " + scannerFile.getAbsolutePath());
      }
    }

    if (scannerFile.exists()) {
      available = true;

      // Apply executable permissions on POSIX filesystem
      if(FileSystemUtils.isPosix()) {
        try {
          Set<PosixFilePermission> executablePermission = PosixFilePermissions.fromString("rwxr-xr--");
          Files.setPosixFilePermissions(scannerFile.toPath(), executablePermission);
        } catch (IOException e) {
          log.error("Permissions can't be applied on file {}", scannerFile.getPath(), e);
        }
      }

    } else {
      log.error("Can't find owlplug scanner executable at {}", scannerFile.getPath());
    }

  }

  @Override
  public void open() {

  }

  @Override
  public List<NativePlugin> loadPlugin(String path) {

    log.debug("Load plugin {}", path);
    if (!isAvailable()) {
      throw new IllegalStateException("Plugin loader must be available");
    }

    try {
      CommandRunner commandRunner = new CommandRunner();
      commandRunner.setTimeoutActivated(true);
      commandRunner.setTimeout(30000); // 30 seconds timeout
      CommandResult result = commandRunner.run(scannerDirectory + SEPARATOR +  scannerId, path);
      log.debug("Response received from scanner");
      log.trace(result.getOutput());

      if (result.getExitValue() >= 0) {

        log.debug("Extracting XML from content received by the scanner");

        if(result.getOutput().contains("<?xml")) {
          String outputXML = result.getOutput().substring(result.getOutput().indexOf("<?xml"));
          log.debug("XML extracted from scanner output");
          log.debug(outputXML);

          JAXBContext jaxbContext = JAXBContext.newInstance(JuceXMLPlugin.class);
          Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
          JuceXMLPlugin juceXmlPlugin = (JuceXMLPlugin) jaxbUnmarshaller.unmarshal(new StringReader(outputXML));

          // TODO retrieve multiple plugins
          ArrayList<NativePlugin> plugins = new ArrayList<>();
          plugins.add(juceXmlPlugin.toNativePlugin());
          return plugins;

        } else {
          log.error("No XML tag can be extracted from scanner output for plugin {}", path);
          log.debug(result.getOutput());
        }


      } else {
        log.debug("Invalid return code {} received from plugin scanner", result.getExitValue());
      }

    } catch (JAXBException e) {
      log.error("Error during XML mapping for native plugin {}", path, e);
    } catch (IOException e) {
      log.error("Error executing plugin scanner {}", path, e);
    }

    return null;
  }

  @Override
  public void close() {

  }

  @Override
  public boolean isAvailable() {
    return available;
  }

  @Override
  public String getName() {
    return "OwlPlug Scanner";
  }

  @Override
  public String getId() {
    return "owlplug-scanner";
  }

  @Override
  public String toString() {
    return this.getName();
  }


  /**
   * Returns platform default executable extension.
   * - Windows host: .exe
   * - Mac host: empty string
   * Returns an empty string for any other hosts
   *
   * @return host default library extension
   */
  private static String getPlatformExecutableExtension() {
    if (OS.WINDOWS.isCurrentOs()) {
      return ".exe";
    }
    return "";
  }

  private static String getPlatformTagName() {
    if (OS.WINDOWS.isCurrentOs()) {
      return "win";
    } else if (OS.MAC.isCurrentOs()) {
      return "osx";
    } else if (OS.LINUX.isCurrentOs()) {
      return "linux";
    }
    return "";
  }

}
