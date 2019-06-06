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

package com.owlplug.host;

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

  public String getDescriptiveName() {
    return descriptiveName;
  }

  public String getPluginFormatName() {
    return pluginFormatName;
  }

  public String getCategory() {
    return category;
  }

  public String getManufacturerName() {
    return manufacturerName;
  }

  public String getVersion() {
    return version;
  }

  public String getFileOrIdentifier() {
    return fileOrIdentifier;
  }

  public int getUid() {
    return uid;
  }

  public boolean isInstrument() {
    return isInstrument;
  }

  public int getNumInputChannels() {
    return numInputChannels;
  }

  public int getNumOutputChannels() {
    return numOutputChannels;
  }

  public boolean isHasSharedContainer() {
    return hasSharedContainer;
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
