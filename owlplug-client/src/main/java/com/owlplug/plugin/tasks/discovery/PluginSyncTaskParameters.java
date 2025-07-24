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

package com.owlplug.plugin.tasks.discovery;

import com.owlplug.core.model.RuntimePlatform;
import java.util.List;

public class PluginSyncTaskParameters {

  private RuntimePlatform platform;
  private String directoryScope;
  private String vst2Directory;
  private String vst3Directory;
  private String auDirectory;
  private String lv2Directory;
  private boolean findVst2;
  private boolean findVst3;
  private boolean findAu;
  private boolean findLv2;
  private List<String> vst2ExtraDirectories;
  private List<String> vst3ExtraDirectories;
  private List<String> auExtraDirectories;
  private List<String> lv2ExtraDirectories;

  public RuntimePlatform getPlatform() {
    return platform;
  }

  public void setPlatform(RuntimePlatform platform) {
    this.platform = platform;
  }
  
  public String getDirectoryScope() {
    return directoryScope;
  }

  public void setDirectoryScope(String directoryScope) {
    this.directoryScope = directoryScope;
  }

  public String getVst2Directory() {
    return vst2Directory;
  }

  public void setVst2Directory(String pluginDirectory) {
    this.vst2Directory = pluginDirectory;
  }

  public String getVst3Directory() {
    return vst3Directory;
  }

  public void setVst3Directory(String vst3Directory) {
    this.vst3Directory = vst3Directory;
  }
  
  public String getAuDirectory() {
    return auDirectory;
  }

  public void setAuDirectory(String auDirectory) {
    this.auDirectory = auDirectory;
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

  public boolean isFindAu() {
    return findAu;
  }

  public void setFindAu(boolean findAu) {
    this.findAu = findAu;
  }

  public List<String> getVst2ExtraDirectories() {
    return vst2ExtraDirectories;
  }

  public void setVst2ExtraDirectories(List<String> vst2ExtraDirectories) {
    this.vst2ExtraDirectories = vst2ExtraDirectories;
  }

  public List<String> getVst3ExtraDirectories() {
    return vst3ExtraDirectories;
  }

  public void setVst3ExtraDirectories(List<String> vst3ExtraDirectories) {
    this.vst3ExtraDirectories = vst3ExtraDirectories;
  }

  public List<String> getAuExtraDirectories() {
    return auExtraDirectories;
  }

  public void setAuExtraDirectories(List<String> auExtraDirectories) {
    this.auExtraDirectories = auExtraDirectories;
  }

  public String getLv2Directory() {
    return lv2Directory;
  }

  public void setLv2Directory(String lv2Directory) {
    this.lv2Directory = lv2Directory;
  }

  public boolean isFindLv2() {
    return findLv2;
  }

  public void setFindLv2(boolean findLv2) {
    this.findLv2 = findLv2;
  }

  public List<String> getLv2ExtraDirectories() {
    return lv2ExtraDirectories;
  }

  public void setLv2ExtraDirectories(List<String> lv2ExtraDirectories) {
    this.lv2ExtraDirectories = lv2ExtraDirectories;
  }

}
