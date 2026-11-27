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
import com.owlplug.core.tasks.TaskException;
import com.owlplug.core.tasks.TaskResult;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.services.ExploreService;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Downloads a package installer and verifies its SHA-256 digest. The installer itself is
 * executed outside of OwlPlug by the operating system, so an installer without a published
 * digest, or with a mismatching one, is always rejected.
 */
public class InstallerDownloadTask extends AbstractBundleDownloadTask {

  private static final String FALLBACK_FILE_NAME = "installer.bin";

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final Path downloadDirectory;
  private Path installerFile;

  /**
   * Creates a new installer download task.
   *
   * @param bundle            installer bundle to download
   * @param downloadDirectory empty directory dedicated to this download
   * @param exploreService    explore service, used to resolve missing OAS file download details
   */
  public InstallerDownloadTask(PackageBundle bundle, Path downloadDirectory, ExploreService exploreService) {
    super(bundle, exploreService);
    this.downloadDirectory = downloadDirectory;
    setName("Download installer - " + bundle.getRemotePackage().getName());
    setMaxProgress(110);
  }

  /**
   * Creates a dedicated download directory for an installer, under the OwlPlug temp directory.
   */
  public static Path createDownloadDirectoryPath() {
    String timestamp = new SimpleDateFormat("ddMMyyHHmmssSSS").format(new Date());
    return Path.of(ApplicationDefaults.getTempDownloadDirectory(), "installers", timestamp);
  }

  @Override
  protected String getMessagePrefix() {
    return "Downloading installer " + bundle.getRemotePackage().getName();
  }

  @Override
  protected TaskResult start() throws Exception {
    try {
      resolveMissingFileDetails();

      String expectedHash = bundle.getDownloadSha256();
      if (expectedHash == null || expectedHash.isBlank()) {
        fail("The registry does not provide a SHA-256 checksum for this installer, "
            + "it can't be verified.");
      }

      this.updateMessage(getMessagePrefix() + " - Downloading files...");
      Path file = download(downloadDirectory.resolve(fileNameFromUrl(bundle.getDownloadUrl())));
      this.commitProgress(100);

      this.updateMessage(getMessagePrefix() + " - Verifying file integrity...");
      if (!verifyHash(file, expectedHash)) {
        fail("The downloaded installer is corrupted or has been tampered with "
            + "(SHA-256 checksum mismatch).");
      }
      this.commitProgress(10);

      installerFile = file;
      this.updateMessage("Installer for " + bundle.getRemotePackage().getName() + " downloaded and verified");
      return completed();

    } catch (TaskException e) {
      cleanup();
      throw e;
    } catch (Exception e) {
      // Includes interruptions caused by a task cancellation
      cleanup();
      if (isCancelled()) {
        throw e;
      }
      this.updateMessage(getMessagePrefix() + " - Failed: " + e.getMessage());
      throw new TaskException("An error occurred while downloading the installer", e);
    }
  }

  private void fail(String message) throws TaskException {
    this.updateMessage(getMessagePrefix() + " - " + message);
    log.error(message);
    this.updateProgress(1, 1);
    throw new TaskException(message);
  }

  /**
   * Deletes the download directory and its content.
   */
  public void cleanup() {
    try {
      if (Files.exists(downloadDirectory)) {
        FileUtils.deleteDirectory(downloadDirectory);
      }
    } catch (IOException e) {
      // The OS may still lock the installer while it runs, the temp directory is cleared later.
      log.warn("Installer download directory {} can't be deleted: {}", downloadDirectory, e.getMessage());
    }
  }

  /**
   * Extracts a safe file name from a download url, keeping its extension so the operating
   * system picks the right handler (.exe, .msi, .pkg, .dmg...).
   *
   * @param url download url
   * @return file name usable in the download directory
   */
  static String fileNameFromUrl(String url) {
    String path;
    try {
      path = new URI(url).getPath();
    } catch (URISyntaxException | NullPointerException e) {
      return FALLBACK_FILE_NAME;
    }
    if (path == null) {
      return FALLBACK_FILE_NAME;
    }
    String name = path.substring(path.lastIndexOf('/') + 1);
    // Keeps only characters safe both as a file name on any supported OS and on the Windows
    // "cmd /c start" command line used to launch installers (no %, &, ^, ! expansion)
    name = name.replaceAll("[^A-Za-z0-9 ._()+-]", "_").trim();
    if (name.isEmpty() || name.startsWith(".") || !name.contains(".")) {
      return FALLBACK_FILE_NAME;
    }
    return name;
  }

  public Path getInstallerFile() {
    return installerFile;
  }

  public PackageBundle getBundle() {
    return bundle;
  }
}
