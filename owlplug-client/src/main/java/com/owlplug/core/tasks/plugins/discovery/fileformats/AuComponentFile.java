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
import com.owlplug.core.model.PluginFormat;
import java.io.File;
import org.apache.commons.io.FilenameUtils;

public class AuComponentFile extends PluginFile {

  public static boolean formatCheck(File file) {
    return (file.getAbsolutePath().endsWith(".component") || file.getAbsolutePath().endsWith(".component.disabled"))
        && file.isDirectory();

  }

  public AuComponentFile(File pluginFile) {
    super(pluginFile);
  }

  @Override
  public Plugin toPlugin() {
    String pluginName = FilenameUtils.removeExtension(this.getPluginFile().getName());
    String pluginPath = this.getPluginFile().getAbsolutePath().replace("\\", "/");
    Plugin plugin = new Plugin(pluginName, pluginPath, PluginFormat.AU);

    File plist = new File(this.getPluginFile().getAbsolutePath() + "/Contents/Info.plist");
    if (plist.exists()) {
      OsxPlistFile plistFile = new OsxPlistFile(plist);
      plistFile.bindProperties(plugin);
    }
    
    plugin.setDisabled(this.isDisabled());

    return plugin;
  }

}
