package com.owlplug.host.loaders;

import com.owlplug.host.JuceXMLPlugin;
import com.owlplug.host.NativePlugin;
import com.owlplug.host.util.ClassPathFileExtractor;
import com.owlplug.host.util.CommandResult;
import com.owlplug.host.util.CommandRunner;
import com.owlplug.host.util.LibraryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class EmbeddedScannerPluginLoader implements NativePluginLoader {

  private static final Logger log = LoggerFactory.getLogger(LibraryLoader.class);

  private static EmbeddedScannerPluginLoader INSTANCE;
  private static String SEPARATOR = System.getProperty("file.separator");

  private static final String DEFAULT_SCANNER_NAME = "owlplug-scanner";
  private static final String DEFAULT_SCANNER_VERSION = "0.0.1";
  private static String DEFAULT_SCANNER_EXT = getPlatformExecutableExtension();
  private static final String DEFAULT_SCANNER_ID = DEFAULT_SCANNER_NAME + "-" + DEFAULT_SCANNER_VERSION + DEFAULT_SCANNER_EXT;

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
    } else {
      log.error("Can't find owlplug scanner executable at {}",scannerFile.getPath());
    }

  }

  @Override
  public void open() {

  }

  @Override
  public NativePlugin loadPlugin(String path) {

    log.debug("Load plugin {}", path);
    if (!isAvailable()) {
      throw new IllegalStateException("Plugin loader must be available");
    }

    try {
      CommandResult result = CommandRunner.run(scannerDirectory + SEPARATOR +  scannerId, path);
      log.trace("Response received from scanner");
      log.trace(result.getOutput());

      if (result.getExitValue() > 0) {
        JAXBContext jaxbContext = JAXBContext.newInstance(JuceXMLPlugin.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JuceXMLPlugin juceXmlPlugin = (JuceXMLPlugin) jaxbUnmarshaller.unmarshal(new StringReader(result.getOutput()));

        return juceXmlPlugin.toNativePlugin();

      } else {
        log.debug("Invalid response from plugin scanner");
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
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.indexOf("win") >= 0) {
      return ".exe";
    }
    return "";
  }

}
