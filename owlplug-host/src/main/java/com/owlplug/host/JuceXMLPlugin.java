package com.owlplug.host;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="PLUGIN")
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
    plugin.setUid(Integer.parseInt(this.uid, 16));
    plugin.setInstrument(this.isInstrument);
    plugin.setNumInputChannels(this.numInputs);
    plugin.setNumOutputChannels(this.numOutputs);
    return plugin;

  }

}
