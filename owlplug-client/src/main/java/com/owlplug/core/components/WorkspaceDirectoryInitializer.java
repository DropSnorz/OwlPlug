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

package com.owlplug.core.components;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.vdurmont.semver4j.Semver;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
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
  private void postConstruct() {
    cleanup();
    setupCustomLogLevel();
  }

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
          log.info("Cleaning outdated workspace data from version " + workspaceVersion + " to match constraint "
              + workspaceMinSemver);
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

  /**
   * Retrieve user defined log level in a logging.properties file on the workspace
   * directory.
   */
  public void setupCustomLogLevel() {

    File workingDirectory = new File(ApplicationDefaults.getUserDataDirectory());
    File loggingFile = new File(workingDirectory, "logging.properties");

    if (loggingFile.exists()) {
      log.info("Found custom logging properties " + loggingFile.getPath());
      List<String> allowedLogLevels = Arrays
          .asList(new String[] { "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF", "ALL" });
      Properties loggingProperties = new Properties();
      try {
        loggingProperties.load(new FileInputStream(loggingFile));
        
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
        loggerList.stream().forEach(logger -> {

          if (loggingProperties.containsKey(logger.getName())) {
            String logLevelStr = loggingProperties.getProperty(logger.getName());
            
            if (allowedLogLevels.parallelStream().anyMatch(logLevelStr::contains)) {
              Level level = Level.toLevel(logLevelStr.toUpperCase());
              logger.setLevel(level);
              log.info("Log level for " + logger.getName() + " set to " + logLevelStr);

            } else {
              log.error("Unknown log level " + logLevelStr + " for logger " + logger.getName());
            }
          }
        });

      } catch (IOException e) {
        log.error("Error while parsing custom log file " + loggingFile.getPath(), e);
      }

    }
  }
}
