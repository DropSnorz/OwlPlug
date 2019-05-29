package com.owlplug.core.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

  private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

  private FileUtils() {
  }

  public static String convertPath(String path) {
    return path.replace("\\", "/");
  }

  public static boolean isFilenameValid(String fileName) {

    return fileName != null && !fileName.equals("") && fileName.matches("[-_.A-Za-z0-9]*");
  }

  public static String sanitizeFileName(String fileName) {

    return fileName.replaceAll("[^-_.A-Za-z0-9]", "");

  }

  public static Collection<File> listUniqueFilesAndDirs(File directory) {


    //Find files
    ArrayList<File> files = new ArrayList<>();
    if (directory.isDirectory()) {
      files.add(directory);
    }

    innerListFiles(files, directory, true);
    return files;

  }

  private static void innerListFiles(List<File> files, File directory, boolean includeSubDirectories) {
    File[] found = directory.listFiles();

    if (found != null) {
      for (File file : found) {
        if (file.isDirectory() && includeSubDirectories) {

          // A Symlink is excluded if his target is a parent directory
          if (Files.isSymbolicLink(file.toPath())) {
            try {
              Path targetPath = Files.readSymbolicLink(file.toPath());
              if (!file.getAbsolutePath().startsWith(targetPath.toString())) {
                files.add(file);
                innerListFiles(files, file, includeSubDirectories);
              }

            } catch (IOException e) {
              // If we fails to read symlink target, we add the symlink be we won't explore inner files.
              files.add(file);
            }
          } else {
            files.add(file);
            innerListFiles(files, file, includeSubDirectories);
          }

        } else {
          files.add(file);

        }
      }
    }
  }

  public static void unzip(String source, String dest) throws IOException {

    // Open the file
    try (ZipFile file = new ZipFile(source)) {
      FileSystem fileSystem = FileSystems.getDefault();
      // Get file entries
      Enumeration<? extends ZipEntry> entries = file.entries();

      // We will unzip files in this folder
      String uncompressedDirectory = dest;
      Files.createDirectory(fileSystem.getPath(uncompressedDirectory));

      // Iterate over entries
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        // If directory then create a new directory in uncompressed folder
        if (entry.isDirectory()) {
          log.debug("Creating Directory:" + uncompressedDirectory + File.separator + entry.getName());
          Files.createDirectories(fileSystem.getPath(uncompressedDirectory + File.separator + entry.getName()));

          // Else create the file
        } else {
          InputStream is = file.getInputStream(entry);
          BufferedInputStream bis = new BufferedInputStream(is);
          String uncompressedFileName = uncompressedDirectory + File.separator + entry.getName();

          new File(uncompressedFileName).getParentFile().mkdirs();

          Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
          Files.createFile(uncompressedFilePath);

          FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);

          byte[] buffer = new byte[2048];
          int read = 0;
          while ((read = bis.read(buffer)) > 0) {
            fileOutput.write(buffer, 0, read);
          }

          fileOutput.close();
          log.debug("Written :" + entry.getName());
        }
      }
    } catch (IOException e) {
      throw e;
    }
  }

  public static void copyDirectory(File source, File target) throws IOException {
    org.apache.commons.io.FileUtils.copyDirectory(source, target);
  }

  public static void deleteDirectory(File source) throws IOException {
    org.apache.commons.io.FileUtils.deleteDirectory(source);
  }

  public static String humanReadableByteCount(long bytes, boolean si) {
    int unit = si ? 1000 : 1024;
    if (bytes < unit)
      return bytes + " B";
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
  }
}
