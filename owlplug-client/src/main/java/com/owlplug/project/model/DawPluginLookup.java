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

package com.owlplug.project.model;

import com.owlplug.plugin.model.Plugin;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class DawPluginLookup {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "daw_plugin_id", referencedColumnName = "id")
  private DawPlugin dawPlugin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plugin_id")
  private Plugin plugin;
  @Enumerated(EnumType.STRING)
  private LookupResult result;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public DawPlugin getDawPlugin() {
    return dawPlugin;
  }

  public void setDawPlugin(DawPlugin dawPlugin) {
    this.dawPlugin = dawPlugin;
  }

  public Plugin getPlugin() {
    return plugin;
  }

  public void setPlugin(Plugin plugin) {
    this.plugin = plugin;
  }

  public LookupResult getResult() {
    return result;
  }

  public void setResult(LookupResult result) {
    this.result = result;
  }
}
