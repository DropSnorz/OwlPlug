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

package com.owlplug.host;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

public class NativePlugin {

  private String name;
  private String descriptiveName;
  private String pluginFormatName;
  private String category;
  private String manufacturerName;
  private String version;
  private String fileOrIdentifier;
  private int uid;
  private boolean isInstrument;
  private int numInputChannels;
  private int numOutputChannels;
  private boolean hasSharedContainer;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescriptiveName() {
    return descriptiveName;
  }

  public void setDescriptiveName(String descriptiveName) {
    this.descriptiveName = descriptiveName;
  }

  public String getPluginFormatName() {
    return pluginFormatName;
  }

  public void setPluginFormatName(String pluginFormatName) {
    this.pluginFormatName = pluginFormatName;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getManufacturerName() {
    return manufacturerName;
  }

  public void setManufacturerName(String manufacturerName) {
    this.manufacturerName = manufacturerName;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getFileOrIdentifier() {
    return fileOrIdentifier;
  }

  public void setFileOrIdentifier(String fileOrIdentifier) {
    this.fileOrIdentifier = fileOrIdentifier;
  }

  public int getUid() {
    return uid;
  }

  public void setUid(int uid) {
    this.uid = uid;
  }

  public boolean isInstrument() {
    return isInstrument;
  }

  public void setInstrument(boolean instrument) {
    isInstrument = instrument;
  }

  public int getNumInputChannels() {
    return numInputChannels;
  }

  public void setNumInputChannels(int numInputChannels) {
    this.numInputChannels = numInputChannels;
  }

  public int getNumOutputChannels() {
    return numOutputChannels;
  }

  public void setNumOutputChannels(int numOutputChannels) {
    this.numOutputChannels = numOutputChannels;
  }

  public boolean isHasSharedContainer() {
    return hasSharedContainer;
  }

  public void setHasSharedContainer(boolean hasSharedContainer) {
    this.hasSharedContainer = hasSharedContainer;
  }

  @Override
  public String toString() {
    return "NativePlugin [name=" + name + ", descriptiveName=" + descriptiveName + ", pluginFormatName="
        + pluginFormatName + ", category=" + category + ", manufacturerName=" + manufacturerName + ", version="
        + version + ", fileOrIdentifier=" + fileOrIdentifier + ", uid=" + uid + ", isInstrument=" + isInstrument
        + ", numInputChannels=" + numInputChannels + ", numOutputChannels=" + numOutputChannels
        + ", hasSharedContainer=" + hasSharedContainer + "]";
  }

  
}
