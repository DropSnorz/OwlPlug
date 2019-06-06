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
 
package com.owlplug.core.tasks;

import com.owlplug.core.dao.PluginDAO;
import com.owlplug.core.model.Plugin;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginRemoveTask extends AbstractTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  protected Plugin plugin;
  protected PluginDAO pluginDAO;

  public PluginRemoveTask(Plugin plugin, PluginDAO pluginDAO) {

    this.plugin = plugin;
    this.pluginDAO = pluginDAO;

    setName("Remove Plugin - " + plugin.getName());
  }

  @Override
  protected TaskResult call() throws Exception {

    this.updateProgress(0, 1);
    this.updateMessage("Deleting plugin " + plugin.getName() + " ...");

    File pluginFile = new File(plugin.getPath());
    boolean fileDeleteSuccess = false;
    if(pluginFile.exists() && pluginFile.isDirectory()) {
      try {
        FileUtils.deleteDirectory(pluginFile);
        fileDeleteSuccess = true;
      } catch (IOException e) {
        log.error("Plugin File can't be removed: " + pluginFile.getPath(), e);
      }
    } else {
      fileDeleteSuccess = pluginFile.delete();
    }
    
    
    if (fileDeleteSuccess) {
      pluginDAO.delete(plugin);
      this.updateProgress(1, 1);
      this.updateMessage("Plugin successfully deleted");

      return null;
    } else {
      this.updateMessage("Error during plugin removal");
      throw new TaskException("Error during plugin removal");

    }

  }

}
