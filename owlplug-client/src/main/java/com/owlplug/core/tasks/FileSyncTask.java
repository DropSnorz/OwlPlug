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

package com.owlplug.core.tasks;

import com.owlplug.core.dao.FileStatDAO;
import com.owlplug.core.model.FileStat;
import com.owlplug.core.utils.FileUtils;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSyncTask extends AbstractTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private FileStatDAO fileStatDAO;

  private List<String> directories;

  public FileSyncTask(FileStatDAO fileStatDAO, String directoryPath) {
    this.fileStatDAO = fileStatDAO;
    directories = Arrays.asList(directoryPath);
    setName("Sync files metrics");
  }

  public FileSyncTask(FileStatDAO fileStatDAO, List<String> directories) {
    this.fileStatDAO = fileStatDAO;
    this.directories = directories;
    setName("Sync files metrics");
  }


  @Override
  protected TaskResult start() throws Exception {

    this.updateProgress(1, 3);

    long length = 0;
    for (String directoryPath : directories) {
      try {
        log.info("Starting file sync task on directory {}", directoryPath);
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
          length = extractFolderSize(directory, null);
          log.info("Completed file sync task on directory {}, computed length: {}", directoryPath, length);
        }

      } catch (Exception e) {
        log.error("An error occurred during file sync task execution", e);
        throw new TaskException(e);
      }
    }
    this.updateMessage("Plugins and files metrics synchronized.");
    this.updateProgress(3, 3);
    return success();
  }

  public long extractFolderSize(File directory, FileStat parent) {
    long length = 0;

    this.updateMessage("Collecting file metrics on directory: " + directory.getAbsolutePath());
    fileStatDAO.deleteByPath(FileUtils.convertPath(directory.getAbsolutePath()));

    FileStat directoryStat = new FileStat();
    directoryStat.setName(directory.getName());
    directoryStat.setPath(FileUtils.convertPath(directory.getAbsolutePath()));

    if (parent != null) {
      directoryStat.setParentPath(parent.getPath());
      directoryStat.setParent(parent);
    }
    directoryStat.setLength(0);
    fileStatDAO.save(directoryStat);

    for (File file : directory.listFiles()) {
      if (file.isFile()) {
        FileStat fileStat = new FileStat();
        fileStat.setName(file.getName());
        fileStat.setPath(FileUtils.convertPath(file.getAbsolutePath()));
        fileStat.setParentPath(FileUtils.convertPath(directory.getAbsolutePath()));
        fileStat.setLength(file.length());
        fileStat.setParent(directoryStat);

        directoryStat.getChilds().add(fileStat);
        length += fileStat.getLength();

      } else {
        length += extractFolderSize(file, directoryStat);
      }
    }

    directoryStat.setLength(length);
    fileStatDAO.save(directoryStat);
    return length;

  }
}
