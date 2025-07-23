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

package com.owlplug.explore.tasks;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.model.RuntimePlatform;
import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskException;
import com.owlplug.core.tasks.TaskResult;
import com.owlplug.core.utils.ArchiveUtils;
import com.owlplug.core.utils.CryptoUtils;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.core.utils.nio.CallbackByteChannel;
import com.owlplug.explore.model.PackageBundle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleInstallTask extends AbstractTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private PackageBundle bundle;
  private File targetDirectory;
  private ApplicationDefaults applicationDefaults;

  /**
   * Creates a new Package Bundle installation task.
   *
   * @param bundle              Bundle to download
   * @param targetDirectory     Target directory where downloaded package is
   *                            stored
   * @param applicationDefaults Owlplug ApplicationDefaults
   */
  public BundleInstallTask(PackageBundle bundle, File targetDirectory, ApplicationDefaults applicationDefaults) {

    this.bundle = bundle;
    this.targetDirectory = targetDirectory;
    this.applicationDefaults = applicationDefaults;
    setName("Install plugin - " + bundle.getRemotePackage().getName());
    setMaxProgress(150);
  }

  @Override
  protected TaskResult start() throws Exception {

    try {
      boolean created = targetDirectory.mkdirs();
      if (!targetDirectory.exists() && !created) {
        this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Can't create installation directory");
        log.error("Can't create installation directory. ");
        throw new TaskException("Can't create installation directory");
      } else if (!targetDirectory.isDirectory()) {
        this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Invalid installation directory");
        log.error("Invalid plugin installation target directory");
        throw new TaskException("Invalid plugin installation target directory");
      }
      this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Downloading files...");
      File archiveFile = downloadInTempDirectory(bundle);

      this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Verifying files...");

      if (bundle.getDownloadSha256() != null && !bundle.getDownloadSha256().isBlank()) {
        log.debug("Verify downloaded file hash for bundle {}", bundle.getName());
        if (!verifyHash(archiveFile, bundle.getDownloadSha256())) {
          String errorMessage = "An error occurred during plugin installation: Downloaded file is invalid, corrupted or can't be verified";
          this.updateMessage(errorMessage);
          log.error(errorMessage);
          archiveFile.delete();
          this.updateProgress(1, 1);
          throw new TaskException(errorMessage);
        }
      }

      this.commitProgress(100);
      this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Extracting files...");
      File extractedArchiveFolder = new File(ApplicationDefaults.getTempDownloadDirectory() + File.separator + "temp-"
                                                 + archiveFile.getName().replace(".owlpack", ""));
      ArchiveUtils.extract(archiveFile.getAbsolutePath(), extractedArchiveFolder.getAbsolutePath());

      this.commitProgress(30);

      this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Moving files...");
      installToPluginDirectory(extractedArchiveFolder, targetDirectory);

      this.commitProgress(20);

      this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Cleaning files...");
      archiveFile.delete();
      FileUtils.deleteDirectory(extractedArchiveFolder);

      this.commitProgress(10);
      this.updateMessage("Plugin " + bundle.getRemotePackage().getName() + " successfully Installed");

    } catch (IOException e) {
      this.updateMessage("An error occurred during plugin install: " + e.getMessage());
      log.error("An error occurred during plugin install: " + e.getMessage());
      this.updateProgress(1, 1);
      throw new TaskException("An error occurred during plugin install", e);
    }

    return success();
  }

  private File downloadInTempDirectory(PackageBundle bundle) throws TaskException {

    URL website;
    try {
      website = new URL(bundle.getDownloadUrl());
    } catch (MalformedURLException e) {
      this.updateMessage("Installation of " + bundle.getRemotePackage().getName() + " canceled: Can't download plugin files");
      throw new TaskException(e);

    }

    SimpleDateFormat horodateFormat = new SimpleDateFormat("ddMMyyhhmmssSSS");
    new File(ApplicationDefaults.getTempDownloadDirectory()).mkdirs();
    String outPutFileName = horodateFormat.format(new Date()) + ".owlpack";
    String outputFilePath = ApplicationDefaults.getTempDownloadDirectory() + File.separator + outPutFileName;
    File outputFile = new File(outputFilePath);

    try (
        CallbackByteChannel rbc = new CallbackByteChannel(Channels.newChannel(website.openStream()),
            contentLength(website));
        FileOutputStream fos = new FileOutputStream(outputFile)) {

      rbc.setCallback(p -> {
        computeTotalProgress(p);
      });
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
      return outputFile;

    } catch (MalformedURLException e) {
      this.updateMessage("Installation of " + bundle.getRemotePackage().getName() + " canceled: Can't download plugin files");
      throw new TaskException(e);
    } catch (FileNotFoundException e) {
      this.updateMessage("Installation of " + bundle.getRemotePackage().getName() + " canceled: File not found");
      throw new TaskException(e);
    } catch (IOException e) {
      this.updateMessage("Installation of " + bundle.getRemotePackage().getName() + " canceled: Can't write file on disk");
      throw new TaskException(e);
    }

  }

  private void installToPluginDirectory(File source, File target) throws IOException {

    OwlPackStructureType structure = getStructureType(source);
    // Choose the folder to copy from the downloaded source
    File newSource = source;
    switch (structure) {
      case NESTED -> newSource = source.listFiles()[0];
      case ENV -> newSource = getSubFileByPlatformTag(source);
      case NESTED_ENV -> newSource = getSubFileByPlatformTag(source.listFiles()[0]);
      default -> log.debug("Can't determine owlpack structure type (NESTED, ENV or NESTED_ENV)."
                               + " Directory will be used as it.");
    }

    FileUtils.copyDirectory(newSource, target);
  }

  private OwlPackStructureType getStructureType(File directory) {

    RuntimePlatform runtimePlatform = applicationDefaults.getRuntimePlatform();
    OwlPackStructureType structure = OwlPackStructureType.DIRECT;

    if (directory.listFiles().length == 1 && directory.listFiles()[0].isDirectory()
            && !runtimePlatform.getCompatiblePlatformsTags().contains(directory.listFiles()[0].getName())) {
      structure = OwlPackStructureType.NESTED;
      for (File f : directory.listFiles()[0].listFiles()) {
        if (runtimePlatform.getCompatiblePlatformsTags().contains(f.getName())) {
          structure = OwlPackStructureType.NESTED_ENV;
        }
      }
    } else if (directory.listFiles().length >= 1) {
      // if the directory describes an environment related bundle
      for (File f : directory.listFiles()) {
        if (runtimePlatform.getCompatiblePlatformsTags().contains(f.getName())) {
          return OwlPackStructureType.ENV;
        }
      }
    }
    return structure;
  }

  private int contentLength(URL url) {
    HttpURLConnection connection;
    int contentLength = -1;
    try {
      connection = (HttpURLConnection) url.openConnection();
      contentLength = connection.getContentLength();
    } catch (Exception e) {
      return 1;
    }
    return contentLength;
  }

  private File getSubFileByPlatformTag(File parent) {

    RuntimePlatform runtimePlatform = applicationDefaults.getRuntimePlatform();
    File[] subFiles = parent.listFiles();

    for (String platformTag : runtimePlatform.getCompatiblePlatformsTags()) {
      for (File f : subFiles) {
        if (f.getName().equals(platformTag)) {
          return f;
        }
      }
    }
    return null;
  }

  private boolean verifyHash(File file, String expectedHash) {

    String fileHash;
    try {
      fileHash = CryptoUtils.getFileSha256Digest(file);
    } catch (IOException e) {
      log.error("File hash can't be computed", e);
      return false;
    }

    if (expectedHash.equalsIgnoreCase(fileHash)) {
      log.debug("Valid SHA256 given: {}, expected: {}", fileHash, expectedHash);
      return true;
    } else {
      log.warn("Invalid SHA256 given: {}, expected: {}", fileHash, expectedHash);
      return false;
    }

  }

  /**
   * Compatible package archive structures.
   * <pre>
   * -------------- DIRECT
   * plugin.zip/
   *   ├── plugin.dll
   *   └── (other required files...)
   *
   * -------------- NESTED
   * plugin.zip/
   *   └── plugin
   *         ├── plugin.dll
   *         └── (other required files...)
   *
   * -------------- NESTED_ENV
   * plugin.zip/
   *   └── plugin
   *         ├── x86
   *         │    ├── plugin.dll
   *         │    └── (other required files...)
   *         └── x64
   *              ├── plugin.dll
   *              └── (other required files...)
   * </pre>
   */
  private enum OwlPackStructureType {
    DIRECT, ENV, NESTED, NESTED_ENV,
  }

}
