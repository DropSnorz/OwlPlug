package com.owlplug.core.model;

import com.owlplug.store.model.StoreProduct;
import javax.persistence.*;

@Entity
@Table(indexes = { @Index(name = "IDX_PLUGIN_COMPONENT_ID", columnList = "id") })
public class PluginComponent {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  protected String name;
  protected String descriptiveName;
  protected String uid;
  protected String category;
  protected String manufacturerName;
  protected String identifier;
  protected String path;
  protected String bundleId;
  protected String version;

  @ManyToOne
  private Plugin plugin;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
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

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getBundleId() {
    return bundleId;
  }

  public void setBundleId(String bundleId) {
    this.bundleId = bundleId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Plugin getPlugin() {
    return plugin;
  }

  public void setPlugin(Plugin plugin) {
    this.plugin = plugin;
  }
}
