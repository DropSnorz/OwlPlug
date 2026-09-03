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
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleInstallTask extends AbstractTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private PackageBundle bundle;
  private Path targetDirectory;
  private ApplicationDefaults applicationDefaults;
  private ExploreService exploreService;

  /**
   * Creates a new Package Bundle installation task.
   *
   * @param bundle              Bundle to download
   * @param targetDirectory     Target directory where downloaded package is
   *                            stored
   * @param applicationDefaults Owlplug ApplicationDefaults
   * @param exploreService      Explore service, used to resolve missing OAS file download details
   */
  public BundleInstallTask(PackageBundle bundle, Path targetDirectory, ApplicationDefaults applicationDefaults,
                            ExploreService exploreService) {

    this.bundle = bundle;
    this.targetDirectory = targetDirectory;
    this.applicationDefaults = applicationDefaults;
    this.exploreService = exploreService;
    setName("Install plugin - " + bundle.getRemotePackage().getName());
    setMaxProgress(150);
  }

  @Override
  protected TaskResult start() throws Exception {

    try {
      try {
        Files.createDirectories(targetDirectory);
      } catch (FileAlreadyExistsException e) {
        // targetDirectory (or a parent segment) exists and is a regular file
        this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Invalid installation directory");
        log.error("Invalid plugin installation target directory", e);
        throw new TaskException("Invalid plugin installation target directory", e);
      }
      resolveMissingFileDetails(bundle);

      this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Downloading files...");
      Path archiveFile = downloadInTempDirectory(bundle);

      this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Verifying files...");

      if (bundle.getDownloadSha256() != null && !bundle.getDownloadSha256().isBlank()) {
        log.debug("Verify downloaded file hash for bundle {}", bundle.getName());
        if (!verifyHash(archiveFile, bundle.getDownloadSha256())) {
          String errorMessage = "An error occurred during plugin installation: Downloaded file is invalid, corrupted or can't be verified";
          this.updateMessage(errorMessage);
          log.error(errorMessage);
          Files.delete(archiveFile);
          this.updateProgress(1, 1);
          throw new TaskException(errorMessage);
        }
      }

      this.commitProgress(100);
      this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Extracting files...");
      Path extractedArchiveFolder = Path.of(ApplicationDefaults.getTempDownloadDirectory())
          .resolve("temp-" + archiveFile.getFileName().toString().replace(".owlpack", ""));
      ArchiveUtils.extract(archiveFile, extractedArchiveFolder);

      this.commitProgress(30);

      this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Moving files...");
      installToPluginDirectory(extractedArchiveFolder, targetDirectory);

      this.commitProgress(20);

      this.updateMessage("Installing plugin " + bundle.getRemotePackage().getName() + " - Cleaning files...");
      Files.delete(archiveFile);
      FileUtils.deleteDirectory(extractedArchiveFolder);

      this.commitProgress(10);
      this.updateMessage("Plugin " + bundle.getRemotePackage().getName() + " successfully Installed");

    } catch (IOException e) {
      this.updateMessage("An error occurred during plugin install: "
                             + e.getClass().getSimpleName() + ": " + e.getMessage());
      log.error("An error occurred during plugin install: {}", e.getMessage(), e);
      this.updateProgress(1, 1);
      throw new TaskException("An error occurred during plugin install", e);
    }

    return completed();
  }

  /**
   * Resolves a bundle's download details from the OAS registry detail endpoint when they were
   * not provided by the bulk registry sync. No-op for bundles that already have a download url,
   * or that don't originate from an OAS_REGISTRY source.
   */
  private void resolveMissingFileDetails(PackageBundle bundle) throws TaskException {

    if (bundle.getDownloadUrl() != null && !bundle.getDownloadUrl().isBlank()) {
      return;
    }

    RemotePackage remotePackage = bundle.getRemotePackage();
    RemoteSource remoteSource = remotePackage.getRemoteSource();
    if (remoteSource == null || remoteSource.getType() != SourceType.OAS_REGISTRY) {
      return;
    }

    this.updateMessage("Installing plugin " + remotePackage.getName() + " - Fetching download details...");
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

  private Path downloadInTempDirectory(PackageBundle bundle) throws TaskException {

    URL website;
    try {
      website = new URL(bundle.getDownloadUrl());
    } catch (MalformedURLException e) {
      this.updateMessage("Installation of " + bundle.getRemotePackage().getName() + " canceled: Can't download plugin files");
      throw new TaskException(e);

    }

    SimpleDateFormat horodateFormat = new SimpleDateFormat("ddMMyyhhmmssSSS");
    Path tempDir = Path.of(ApplicationDefaults.getTempDownloadDirectory());
    String outPutFileName = horodateFormat.format(new Date()) + ".owlpack";
    Path outputFile = tempDir.resolve(outPutFileName);

    try (
        CallbackByteChannel rbc = new CallbackByteChannel(Channels.newChannel(website.openStream()),
            contentLength(website));
        FileChannel fos = openOutputChannel(tempDir, outputFile)) {

      rbc.setCallback(p -> computeTotalProgress(p));
      fos.transferFrom(rbc, 0, Long.MAX_VALUE);
      return outputFile;

    } catch (MalformedURLException e) {
      this.updateMessage("Installation of " + bundle.getRemotePackage().getName() + " canceled: Can't download plugin files");
      throw new TaskException(e);
    } catch (IOException e) {
      this.updateMessage("Installation of " + bundle.getRemotePackage().getName() + " canceled: Can't write file on disk");
      throw new TaskException(e);
    }

  }

  /**
   * Opens a writable channel to the given output file, creating the parent
   * temp directory first if needed.
   */
  private FileChannel openOutputChannel(Path tempDir, Path outputFile) throws IOException {
    Files.createDirectories(tempDir);
    return FileChannel.open(outputFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
  }

  private void installToPluginDirectory(Path source, Path target) throws IOException {

    OwlPackStructureType structure = getStructureType(source);
    // Choose the folder to copy from the downloaded source
    Path newSource = source;
    switch (structure) {
      case NESTED -> newSource = listDirectory(source).get(0);
      case ENV -> newSource = getSubFileByPlatformTag(source);
      case NESTED_ENV -> newSource = getSubFileByPlatformTag(listDirectory(source).get(0));
      default -> log.debug("Can't determine owlpack structure type (NESTED, ENV or NESTED_ENV)."
                               + " Directory will be used as it.");
    }

    FileUtils.copyDirectory(newSource, target);
  }

  private OwlPackStructureType getStructureType(Path directory) throws IOException {

    RuntimePlatform runtimePlatform = applicationDefaults.getRuntimePlatform();
    OwlPackStructureType structure = OwlPackStructureType.DIRECT;

    List<Path> entries = listDirectory(directory);
    if (entries.size() == 1 && Files.isDirectory(entries.get(0))
            && !runtimePlatform.getCompatiblePlatformsTags().contains(entries.get(0).getFileName().toString())) {
      structure = OwlPackStructureType.NESTED;
      for (Path f : listDirectory(entries.get(0))) {
        if (runtimePlatform.getCompatiblePlatformsTags().contains(f.getFileName().toString())) {
          structure = OwlPackStructureType.NESTED_ENV;
        }
      }
    } else if (entries.size() >= 1) {
      // if the directory describes an environment related bundle
      for (Path f : entries) {
        if (runtimePlatform.getCompatiblePlatformsTags().contains(f.getFileName().toString())) {
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

  private Path getSubFileByPlatformTag(Path parent) throws IOException {

    RuntimePlatform runtimePlatform = applicationDefaults.getRuntimePlatform();
    List<Path> subFiles = listDirectory(parent);

    for (String platformTag : runtimePlatform.getCompatiblePlatformsTags()) {
      for (Path f : subFiles) {
        if (f.getFileName().toString().equals(platformTag)) {
          return f;
        }
      }
    }
    return null;
  }

  /**
   * Lists the direct children of a directory as a {@link Path} list.
   * {@code Files.list} returns a stream that must be closed, hence the
   * try-with-resources.
   */
  private List<Path> listDirectory(Path dir) throws IOException {
    try (var stream = Files.list(dir)) {
      return stream.collect(Collectors.toList());
    }
  }

  private boolean verifyHash(Path file, String expectedHash) {

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
