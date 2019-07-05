/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.store.tasks;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.model.platform.RuntimePlatform;
import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskException;
import com.owlplug.core.tasks.TaskResult;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.core.utils.nio.CallbackByteChannel;
import com.owlplug.store.model.ProductBundle;
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

public class ProductInstallTask extends AbstractTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private ProductBundle bundle;
  private File targetDirectory;
  private ApplicationDefaults applicationDefaults;

  /**
   * Creates a new Product Installation task.
   * 
   * @param bundle              Bundle to download
   * @param targetDirectory     Target directory where downloaded product is
   *                            stored
   * @param applicationDefaults Ownplug ApplicationDefaults
   */
  public ProductInstallTask(ProductBundle bundle, File targetDirectory, ApplicationDefaults applicationDefaults) {

    this.bundle = bundle;
    this.targetDirectory = targetDirectory;
    this.applicationDefaults = applicationDefaults;
    setName("Install plugin - " + bundle.getProduct().getName());
    setMaxProgress(150);
  }

  @Override
  protected TaskResult call() throws Exception {

    try {
      boolean created = targetDirectory.mkdirs();
      if (!targetDirectory.exists() && !created) {
          this.updateMessage("Installing plugin " + bundle.getProduct().getName() + " - Can't create installation directory");
          log.error("Can't create installation directory. ");
          throw new TaskException("Can't create installation directory");
      }
      else if (!targetDirectory.isDirectory()) {
        this.updateMessage("Installing plugin " + bundle.getProduct().getName() + " - Invalid installation directory");
        log.error("Invalid plugin installation target directory");
        throw new TaskException("Invalid plugin installation target directory");
      }
      this.updateMessage("Installing plugin " + bundle.getProduct().getName() + " - Downloading files...");
      File archiveFile = downloadInTempDirectory(bundle);

      this.commitProgress(100);
      this.updateMessage("Installing plugin " + bundle.getProduct().getName() + " - Extracting files...");
      File extractedArchiveFolder = new File(applicationDefaults.getTempDowloadDirectory() + "/" + "temp-"
          + archiveFile.getName().replace(".owlpack", ""));
      FileUtils.unzip(archiveFile.getAbsolutePath(), extractedArchiveFolder.getAbsolutePath());

      this.commitProgress(30);

      this.updateMessage("Installing plugin " + bundle.getProduct().getName() + " - Moving files...");
      installToPluginDirectory(extractedArchiveFolder, targetDirectory);

      this.commitProgress(20);

      this.updateMessage("Installing plugin " + bundle.getProduct().getName() + " - Cleaning files...");
      archiveFile.delete();
      FileUtils.deleteDirectory(extractedArchiveFolder);

      this.commitProgress(10);
      this.updateMessage("Plugin " + bundle.getProduct().getName() + " successfully Installed");

    } catch (IOException e) {
      this.updateProgress(1, 1);
      throw new TaskException(e);
    }

    return success();
  }

  private File downloadInTempDirectory(ProductBundle bundle) throws TaskException {

    URL website;
    try {
      website = new URL(bundle.getDownloadUrl());
    } catch (MalformedURLException e) {
      this.updateMessage("Installation of " + bundle.getProduct().getName() + " canceled: Can't download plugin files");
      throw new TaskException(e);

    }

    SimpleDateFormat horodateFormat = new SimpleDateFormat("ddMMyyhhmmssSSS");
    new File(ApplicationDefaults.getTempDowloadDirectory()).mkdirs();
    String outPutFileName = horodateFormat.format(new Date()) + ".owlpack";
    String outputFilePath = ApplicationDefaults.getTempDowloadDirectory() + File.separator + outPutFileName;
    File outputFile = new File(outputFilePath);

    try (
        CallbackByteChannel rbc = new CallbackByteChannel(Channels.newChannel(website.openStream()),
            contentLength(website));
        FileOutputStream fos = new FileOutputStream(outputFile)) {

      rbc.setCallback(p -> {
        log.debug(String.valueOf(p));
        computeTotalProgress(p);
      });
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
      return outputFile;

    } catch (MalformedURLException e) {
      this.updateMessage("Installation of " + bundle.getProduct().getName() + " canceled: Can't download plugin files");
      throw new TaskException(e);
    } catch (FileNotFoundException e) {
      this.updateMessage("Installation of " + bundle.getProduct().getName() + " canceled: File not found");
      throw new TaskException(e);
    } catch (IOException e) {
      this.updateMessage("Installation of " + bundle.getProduct().getName() + " canceled: Can't write file on disk");
      throw new TaskException(e);
    }

  }

  private void installToPluginDirectory(File source, File target) throws IOException {

    OwlPackStructureType structure = getStructureType(source);
    // Choose the folder to copy from the downloaded source
    File newSource = source;
    switch (structure) {
    case NESTED:
      newSource = source.listFiles()[0];
      break;
    case ENV:
      newSource = getSubfileByPlatformTag(source);
      break;
    case NESTED_ENV:
      newSource = getSubfileByPlatformTag(source.listFiles()[0]);
      break;
    default:
      break;
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
      // if the directory describes an environement related bundle
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

  private File getSubfileByPlatformTag(File parent) {

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

  private File getSubfileByName(File parent, String filename) {
    for (File f : parent.listFiles()) {
      if (f.getName().equals(filename)) {
        return f;
      }
    }
    return null;
  }

  /**
   * Compatible product archive structues -------------- DIRECT plugin.zip/ ├──
   * plugin.dll └── (other required files...) -------------- NESTED plugin.zip/
   * └── plugin ├── plugin.dll └── (other required files...) --------------
   * NESTED_ENV plugin.zip/ └── plugin ├── x86 │ ├── plugin.dll │ └── (other
   * required files...) └── x64 ├── plugin.dll └── (other required files...)
   *
   *
   */
  private enum OwlPackStructureType {
    DIRECT, ENV, NESTED, NESTED_ENV,
  }

}
