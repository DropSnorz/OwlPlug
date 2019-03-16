package com.owlplug.host;

public class NativePlugin {
  
  private String path;
  private String name;
  private int uid;
  private String version;
  private String manufacturerName;
  
  
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getUid() {
    return uid;
  }

  public void setUid(int uid) {
    this.uid = uid;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getManufacturerName() {
    return manufacturerName;
  }

  public void setManufacturerName(String manufacturerName) {
    this.manufacturerName = manufacturerName;
  }


  @Override
  public String toString() {
    return "NativePlugin [path=" + path + ", name=" + name + ", uid=" + uid + ", version=" + version
        + ", manufacturerName=" + manufacturerName + "]";
  }
  
}
