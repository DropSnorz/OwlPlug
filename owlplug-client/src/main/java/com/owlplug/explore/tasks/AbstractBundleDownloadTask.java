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

import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskException;
import com.owlplug.core.utils.CryptoUtils;
import com.owlplug.core.utils.nio.CallbackByteChannel;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.model.SourceType;
import com.owlplug.explore.model.mappers.oas.OASFile;
import com.owlplug.explore.services.ExploreService;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base task for downloading a {@link PackageBundle} file: resolves lazily provided download
 * details, streams the file to disk with progress reporting and verifies its SHA-256 digest.
 */
public abstract class AbstractBundleDownloadTask extends AbstractTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  protected final PackageBundle bundle;
  protected final ExploreService exploreService;

  protected AbstractBundleDownloadTask(PackageBundle bundle, ExploreService exploreService) {
    this.bundle = bundle;
    this.exploreService = exploreService;
  }

  /**
   * Prefix of task status messages, for example "Installing plugin X".
   */
  protected abstract String getMessagePrefix();

  /**
   * Resolves a bundle's download details from the OAS registry detail endpoint when they were
   * not provided by the bulk registry sync. No-op for bundles that already have a download url,
   * or that don't originate from an OAS_REGISTRY source.
   */
  protected void resolveMissingFileDetails() throws TaskException {

    if (bundle.getDownloadUrl() != null && !bundle.getDownloadUrl().isBlank()) {
      return;
    }

    RemotePackage remotePackage = bundle.getRemotePackage();
    RemoteSource remoteSource = remotePackage.getRemoteSource();
    if (remoteSource == null || remoteSource.getType() != SourceType.OAS_REGISTRY) {
      return;
    }

    this.updateMessage(getMessagePrefix() + " - Fetching download details...");
    OASFile file = exploreService.fetchOASFile(remoteSource, remotePackage, bundle);

    if (file == null || file.getUrl() == null) {
      String errorMessage = "An error occurred during plugin installation: "
          + "Can't retrieve download details from registry";
      this.updateMessage(errorMessage);
      log.error(errorMessage);
      throw new TaskException(errorMessage);
    }

    bundle.setDownloadUrl(file.getUrl());
    bundle.setDownloadSha256(file.getSha256());
  }

  /**
   * Downloads the bundle file to the given output file, reporting progress from 0 to 100 on
   * top of the currently committed progress.
   *
   * @param outputFile file to write, parent directories are created if needed
   * @return the output file
   * @throws TaskException if the file can't be downloaded or written
   */
  protected Path download(Path outputFile) throws TaskException {

    URL website;
    try {
      website = new URL(bundle.getDownloadUrl());
    } catch (MalformedURLException e) {
      this.updateMessage(getMessagePrefix() + " - Canceled: Can't download plugin files");
      throw new TaskException(e);
    }

    try (
        CallbackByteChannel rbc = new CallbackByteChannel(Channels.newChannel(website.openStream()),
            contentLength(website));
        FileChannel fos = openOutputChannel(outputFile)) {

      rbc.setCallback(p -> computeTotalProgress(p));
      fos.transferFrom(rbc, 0, Long.MAX_VALUE);
      return outputFile;

    } catch (MalformedURLException e) {
      this.updateMessage(getMessagePrefix() + " - Canceled: Can't download plugin files");
      throw new TaskException(e);
    } catch (IOException e) {
      this.updateMessage(getMessagePrefix() + " - Canceled: Can't write file on disk");
      throw new TaskException(e);
    }
  }

  /**
   * Opens a writable channel to the given output file, creating its parent directory first
   * if needed.
   */
  private FileChannel openOutputChannel(Path outputFile) throws IOException {
    Files.createDirectories(outputFile.getParent());
    return FileChannel.open(outputFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING);
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

  /**
   * Returns true if the file SHA-256 digest matches the expected one (case-insensitive).
   */
  protected boolean verifyHash(Path file, String expectedHash) {

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
}
