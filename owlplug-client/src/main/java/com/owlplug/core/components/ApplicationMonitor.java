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

import com.owlplug.core.model.ApplicationState;
import java.util.prefs.Preferences;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMonitor {
  
  private final Logger log = LoggerFactory.getLogger(this.getClass());
  
  @Autowired
  protected Preferences preferences;
  private boolean previousExecutionSafelyTerminated = true;
  
  
  @PostConstruct
  private void initialize() {
    
    log.info("Application monitor started");
    ApplicationState lastState = this.getState();
    if (lastState.equals(ApplicationState.RUNNING)) {
      log.info("Previous application instance not terminated safely");
      previousExecutionSafelyTerminated = false;
    }
    
    preferences.put(ApplicationDefaults.APPLICATION_STATE_KEY, ApplicationState.RUNNING.getText());
  }
  
  @PreDestroy
  private void destroy() {
    log.info("Application monitor received shutdown event");
    preferences.put(ApplicationDefaults.APPLICATION_STATE_KEY, ApplicationState.TERMINATED.getText());
    
  }
  
  /**
   * Return last saved {@link ApplicationState}.
   * @return state
   */
  public ApplicationState getState() {
    String stateValue = preferences.get(ApplicationDefaults.APPLICATION_STATE_KEY, "UNKNOWN");
    return ApplicationState.fromString(stateValue);
    
  }

  public boolean isPreviousExecutionSafelyTerminated() {
    return previousExecutionSafelyTerminated;
  }
  
}
