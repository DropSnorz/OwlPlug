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
 
package com.owlplug.core.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

  private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

  private FileUtils() {
  }

  public static String convertPath(String path) {
    return path.replace("\\", "/");
  }

  /**
   * Expands a leading "~" in a path to the current user's home directory.
   * A leading "~" is a shell convention that Java does not resolve on its own, so paths
   * entered or pasted with it (e.g. "~/.vst3") would otherwise be treated as a literal,
   * non-existent relative directory named "~".
   *
   * @param path - path possibly starting with "~"
   * @return path with a leading "~" expanded to the user home directory, unchanged otherwise
   */
  public static String expandUserHome(String path) {
    boolean hasHomePrefix = path != null
        && (path.equals("~") || path.startsWith("~/") || path.startsWith("~\\"));
    if (!hasHomePrefix) {
      return path;
    }
    String userHome = System.getProperty("user.home");
    return userHome + path.substring(1);
  }

  public static String sanitizeFileName(String fileName) {

    return fileName.replaceAll("[^-_.A-Za-z0-9 ]", "").trim().replaceAll("\\s+"," ");

  }

  /**
   * Maximum directory depth explored from the root directory. Prevents runaway
   * exploration on unusual filesystems where loops can't be detected.
   */
  private static final int MAX_DEPTH = 64;

  /**
   * Recursively finds files and directories in a given directory, following symlinks.
   * Each real directory is explored at most once, whatever the number of paths leading to it
   * (symlinks, junctions, bind mounts). Physical directories are explored before symlinked
   * ones, so a file reachable both ways is reported under its physical path.
   * Symlinks are always included in the result, even when their target is not explored again.
   *
   * @param directory - Directory path
   * @return files and directories found, including the root directory
   */
  public static Collection<File> listUniqueFilesAndDirs(File directory) {

    List<File> files = new ArrayList<>();
    if (!directory.isDirectory()) {
      return files;
    }
    files.add(directory);

    Set<Object> visitedDirectories = new HashSet<>();
    Deque<DirectoryEntry> physicalDirectories = new ArrayDeque<>();
    Deque<DirectoryEntry> symlinkedDirectories = new ArrayDeque<>();
    boolean depthLimitReached = false;

    if (markVisited(directory, visitedDirectories)) {
      physicalDirectories.push(new DirectoryEntry(directory, 0));
    }

    while (!physicalDirectories.isEmpty() || !symlinkedDirectories.isEmpty()) {
      DirectoryEntry current;
      if (!physicalDirectories.isEmpty()) {
        current = physicalDirectories.pop();
      } else {
        current = symlinkedDirectories.pollFirst();
        // Target already explored through another path
        if (!markVisited(current.file(), visitedDirectories)) {
          continue;
        }
      }

      File[] found = current.file().listFiles();
      if (found == null) {
        continue;
      }

      for (File file : found) {
        files.add(file);

        if (!file.isDirectory()) {
          continue;
        }

        if (current.depth() >= MAX_DEPTH) {
          if (!depthLimitReached) {
            depthLimitReached = true;
            log.warn("Max depth of {} reached while exploring {}, deeper directories are ignored",
                MAX_DEPTH, directory.getPath());
          }
          continue;
        }

        DirectoryEntry child = new DirectoryEntry(file, current.depth() + 1);
        if (Files.isSymbolicLink(file.toPath())) {
          // Symlinked directories are explored once all physical directories are known
          symlinkedDirectories.addLast(child);
        } else if (markVisited(file, visitedDirectories)) {
          physicalDirectories.push(child);
        }
      }
    }

    return files;

  }

  /**
   * Registers a directory as visited using an identity that doesn't depend on the path used
   * to reach it (file key on POSIX filesystems, real path otherwise).
   *
   * @param directory - directory to register
   * @param visitedDirectories - identities of directories already visited
   * @return true if the directory was not visited yet
   */
  private static boolean markVisited(File directory, Set<Object> visitedDirectories) {
    try {
      Path path = directory.toPath();
      Object key = Files.readAttributes(path, BasicFileAttributes.class).fileKey();
      if (key == null) {
        key = path.toRealPath().toString();
      }
      return visitedDirectories.add(key);
    } catch (IOException e) {
      log.debug("Directory can't be resolved and won't be explored: {}", directory.getPath());
      return false;
    }
  }

  private record DirectoryEntry(File file, int depth) {
  }

  /**
   * @deprecated use {@link #copyDirectory(Path, Path)}
   */
  @Deprecated
  public static void copyDirectory(File source, File target) throws IOException {
    copyDirectory(source.toPath(), target.toPath());
  }

  public static void copyDirectory(Path source, Path target) throws IOException {
    org.apache.commons.io.FileUtils.copyDirectory(source.toFile(), target.toFile());
  }

  /**
   * @deprecated use {@link #deleteDirectory(Path)}
   */
  @Deprecated
  public static void deleteDirectory(File source) throws IOException {
    deleteDirectory(source.toPath());
  }

  public static void deleteDirectory(Path source) throws IOException {
    org.apache.commons.io.FileUtils.deleteDirectory(source.toFile());
  }

  public static String getParentDirectoryName(String path) {
    File file = new File(path);
    File parentFile = file.getParentFile();

    if (file.exists() && parentFile != null && parentFile.exists()) {
      return parentFile.getName();
    }

    return null;

  }

  public static String getFilename(String path) {
    if (path == null || path.isEmpty()) {
      return null;
    }

    File file = new File(path);
    return file.getName();

  }

  public static String humanReadableByteCount(long bytes, boolean si) {
    int unit = si ? 1000 : 1024;
    if (bytes < unit) {
      return bytes + " B";
    }
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
  }
}
