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
import com.owlplug.core.model.PluginFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lv2BundleFile extends PluginFile {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  /**
   * Type checking function against current format for the given file.
   * @param file - file to test
   * @return true if the file matches the current file format
   */
  public static boolean formatCheck(File file) {
    return (file.getAbsolutePath().endsWith(".lv2") || file.getAbsolutePath().endsWith(".lv2.disabled"))
      && file.isDirectory();

  }

  public Lv2BundleFile(File file) {
    super(file);
  }

  @Override
  public Plugin toPlugin() {

    Plugin plugin = createPlugin();
    plugin.setFormat(PluginFormat.LV2);

    return plugin;
  }

  @Override
  public List<PluginComponent> toComponents() {

    File manifest = new File(getPluginFile().getAbsolutePath(), "manifest.ttl");
    List<PluginComponent> components = new ArrayList<>();

    try {
      Model model = ModelFactory.createDefaultModel();
      model.read(new FileInputStream(manifest),null,"TTL");

      for (Resource r : model.listSubjects().toList()) {
        PluginComponent component = new PluginComponent();
        component.setIdentifier(r.getURI());
        component.setName(r.getURI());
        components.add(component);

      }

    } catch (FileNotFoundException e) {
      log.error("Components can't be retrieved",e);
    }
    return components;
  }

}
