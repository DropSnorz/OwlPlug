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
 
package com.owlplug.core.tasks.plugins.discovery.fileformats;

import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginComponent;
import com.owlplug.core.utils.FileUtils;
import java.io.File;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;

public abstract class PluginFile {
  
  private File pluginFile;

  private File scanDirectory;
  
  public PluginFile(File pluginFile) {
    this.pluginFile = pluginFile;
  }
  
  public abstract Plugin toPlugin();

  Plugin createPlugin() {
    Plugin plugin = new Plugin();
    plugin.setScanDirectoryPath(FileUtils.convertPath(this.getScanDirectory().getAbsolutePath()));
    plugin.setPath(FileUtils.convertPath(this.getPluginFile().getAbsolutePath()));
    plugin.setName(FilenameUtils.removeExtension(this.getPluginFile().getName()));
    plugin.setDisabled(this.isDisabled());

    return plugin;
  }

  public List<PluginComponent> toComponents() {
    return null;
  }

  public File getPluginFile() {
    return pluginFile;
  }

  public void setPluginFile(File pluginFile) {
    this.pluginFile = pluginFile;
  }

  public File getScanDirectory() {
    return scanDirectory;
  }

  public void setScanDirectory(File scanDirectory) {
    this.scanDirectory = scanDirectory;
  }

  public boolean isDisabled() {
    return pluginFile.getAbsolutePath().endsWith(".disabled");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PluginFile that = (PluginFile) o;
    return Objects.equals(pluginFile.getAbsolutePath(), that.pluginFile.getAbsolutePath());
  }

  @Override
  public int hashCode() {
    return pluginFile.getAbsolutePath().hashCode();
  }
}
