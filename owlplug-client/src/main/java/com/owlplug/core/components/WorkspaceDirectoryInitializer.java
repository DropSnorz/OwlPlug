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
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Initializes workspace directory settings before the datasource is created,
 * such as user defined log levels.
 */
@Component("workspaceDirectoryInitializer")
public class WorkspaceDirectoryInitializer {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @PostConstruct
  private void postConstruct() {
    setupCustomLogLevel();
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
          .asList("TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF", "ALL");
      Properties loggingProperties = new Properties();
      try {
        loggingProperties.load(new FileInputStream(loggingFile));
        
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
        loggerList.forEach(logger -> {

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
