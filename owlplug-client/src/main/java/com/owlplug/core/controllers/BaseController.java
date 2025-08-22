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

package com.owlplug.core.controllers;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ApplicationPreferences;
import com.owlplug.core.components.DialogManager;
import com.owlplug.core.services.TelemetryService;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {
  
  @Autowired
  private ApplicationDefaults applicationDefaults;
  @Autowired
  private ApplicationPreferences preferences;
  @Autowired
  private TelemetryService telemetryService;
  @Autowired
  private DialogManager dialogManager;
  
  
  public ApplicationDefaults getApplicationDefaults() {
    return applicationDefaults;
  }
  
  public ApplicationPreferences getPreferences() {
    return preferences;
  }
  
  public TelemetryService getTelemetryService() {
    return telemetryService;
  }

  public DialogManager getDialogManager() {
    return dialogManager;
  }
  

}
