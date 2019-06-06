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
 
package com.owlplug.core.components;

import com.vdurmont.semver4j.Semver;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Performs data cleanup on the workspace directory User data migration are not
 * supported for now. Database schema is updated using hibernate auto-ddl=update
 * feature which is limited in case of major relational changes.
 * <p>
 * When the schema can't be updated by hibernate the database will be flushed.
 * </p>
 *
 */
@Component("workspaceDirectoryInitializer")
public class WorkspaceDirectoryInitializer {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ApplicationDefaults applicationDefaults;

  @PostConstruct
  public void cleanup() {

    File workingDirectory = new File(ApplicationDefaults.getUserDataDirectory());

    if (!workingDirectory.exists()) {
      workingDirectory.mkdirs();
    }

    File versionFile = new File(workingDirectory, ".version");
    if (versionFile.exists()) {

      try {

        String workspaceVersion = FileUtils.readFileToString(versionFile);
        Semver workspaceSemver = new Semver(workspaceVersion);
        Semver workspaceMinSemver = new Semver(applicationDefaults.getEnvProperty("owlplug.workspace.min-version"));

        if (workspaceSemver.isLowerThan(workspaceMinSemver)) {
          log.info("Cleanning outdated workspace data from version "
              + workspaceVersion + " to match constraint " + workspaceMinSemver);
          File dbFile = new File(workingDirectory, "owlplug.mv.db");
          dbFile.delete();
        }

      } catch (IOException e) {
        log.error("Workspace version can't be retrieved from file", e);
      }

      versionFile.delete();
    }

    try {
      String currentVersion = applicationDefaults.getVersion();
      versionFile.createNewFile();
      FileUtils.writeStringToFile(versionFile, currentVersion);
    } catch (IOException e) {
      log.error("Version file can't be created in workspace directory", e);
    }

  }

}
