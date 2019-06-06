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
 
package com.owlplug.core.tasks.plugins.discovery;

import com.owlplug.core.model.platform.RuntimePlatform;

public class PluginSyncTaskParameters {

  private RuntimePlatform platform;
  private String vstDirectory;
  private String vst3Directory;
  private boolean findVst2;
  private boolean findVst3;

  public RuntimePlatform getPlatform() {
    return platform;
  }

  public void setPlatform(RuntimePlatform platform) {
    this.platform = platform;
  }

  public String getVstDirectory() {
    return vstDirectory;
  }

  public void setVstDirectory(String pluginDirectory) {
    this.vstDirectory = pluginDirectory;
  }

  public String getVst3Directory() {
    return vst3Directory;
  }

  public void setVst3Directory(String vst3Directory) {
    this.vst3Directory = vst3Directory;
  }

  public boolean isFindVst2() {
    return findVst2;
  }

  public void setFindVst2(boolean findVst2) {
    this.findVst2 = findVst2;
  }

  public boolean isFindVst3() {
    return findVst3;
  }

  public void setFindVst3(boolean findVst3) {
    this.findVst3 = findVst3;
  }

}
