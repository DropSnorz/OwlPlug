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

import com.owlplug.core.utils.PlatformUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import org.springframework.stereotype.Controller;

@Controller
public class PreloaderController {
  
  @FXML
  private Hyperlink owlplugHyperlink;
  @FXML
  private Hyperlink statusHyperlink;
  @FXML
  private Hyperlink documentationHyperlink;
  @FXML
  private Hyperlink roadmapHyperlink;
  
  /**
   * FXML initialize.
   */
  public void initialize() {
    
    owlplugHyperlink.setOnAction((e) -> PlatformUtils.openDefaultBrowser("https://owlplug.com"));
    statusHyperlink.setOnAction((e) -> PlatformUtils.openDefaultBrowser("http://status.owlplug.com"));
    roadmapHyperlink.setOnAction((e) -> PlatformUtils.openDefaultBrowser("https://owlplug.com/roadmap"));
    documentationHyperlink.setOnAction((e) -> PlatformUtils.openDefaultBrowser("https://github.com/DropSnorz/OwlPlug/wiki"));

  }

}
