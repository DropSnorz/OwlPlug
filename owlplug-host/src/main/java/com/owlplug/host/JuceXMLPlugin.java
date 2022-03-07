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

@XmlRootElement(name = "PLUGIN")
public class JuceXMLPlugin {

  @XmlAttribute(name = "name")
  private String name;
  @XmlAttribute(name = "descriptiveName")
  private String descriptiveName;
  @XmlAttribute(name = "format")
  private String format;
  @XmlAttribute(name = "category")
  private String category;
  @XmlAttribute(name = "manufacturer")
  private String manufacturer;
  @XmlAttribute(name = "version")
  private String version;
  @XmlAttribute(name = "file")
  private String file;
  @XmlAttribute(name = "uid")
  private String uid;
  @XmlAttribute(name = "uniqueId")
  private String uniqueId;
  @XmlAttribute(name = "isInstrument")
  private boolean isInstrument;
  @XmlAttribute(name = "fileTime")
  private String fileTime;
  @XmlAttribute(name = "infoUpdateTime")
  private String infoUpdateTime;
  @XmlAttribute(name = "numInputs")
  private int numInputs;
  @XmlAttribute(name = "numOutputs")
  private int numOutputs;
  @XmlAttribute(name = "isShell")
  private boolean isShell;

  public NativePlugin toNativePlugin() {
    NativePlugin plugin = new NativePlugin();
    plugin.setName(this.name);
    plugin.setDescriptiveName(descriptiveName);
    plugin.setPluginFormatName(this.format);
    plugin.setCategory(this.category);
    plugin.setManufacturerName(this.manufacturer);
    plugin.setVersion(this.version);
    plugin.setFileOrIdentifier(this.file);
    plugin.setUid((int) Long.parseLong(this.uid, 16)); // / Parse signed int from HEX
    plugin.setInstrument(this.isInstrument);
    plugin.setNumInputChannels(this.numInputs);
    plugin.setNumOutputChannels(this.numOutputs);
    return plugin;

  }

}
