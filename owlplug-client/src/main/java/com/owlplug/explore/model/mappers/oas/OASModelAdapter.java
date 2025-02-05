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

package com.owlplug.explore.model.mappers.oas;

import com.owlplug.core.model.PluginType;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.PackageTag;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.RemoteSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class OASModelAdapter {

  /**
   * Creates a {@link RemoteSource} entity from a {@link OASRegistry}.
   *
   * @param registry registry json mapper
   * @return pluginStoreEntity
   */
  public static RemoteSource mapperToEntity(OASRegistry registry) {

    RemoteSource remoteSource = new RemoteSource();
    remoteSource.setName(registry.getName());
    remoteSource.setDisplayUrl(registry.getUrl());
    return remoteSource;
  }

  /**
   * Creates a {@link RemotePackage} entity from a {@link OASPlugin}.
   *
   * @param plugin product json mapper
   * @return product entity
   */
  public static RemotePackage mapperToEntity(OASPlugin plugin) {

    RemotePackage remotePackage = new RemotePackage();
    remotePackage.setName(plugin.getName());
    remotePackage.setPageUrl(plugin.getUrl());
    remotePackage.setScreenshotUrl(plugin.getImage());
    remotePackage.setCreator(plugin.getAuthor());
    remotePackage.setLicense(plugin.getLicense());
    remotePackage.setDescription(plugin.getDescription());

    // Only supports effect and instrument plugin type
    if (plugin.getType() != null
            && PluginType.fromString(plugin.getType()) != null) {
      remotePackage.setType(PluginType.fromString(plugin.getType()));
    }

    HashSet<PackageBundle> bundles = new HashSet<>();
    if (plugin.getFiles() != null) {
      for (OASFile file : plugin.getFiles()) {
        // OwlPlug only supports archives
        if (file.getType().equals("archive")) {
          PackageBundle bundle = mapperToEntity(file);
          bundle.setRemotePackage(remotePackage);
          bundles.add(bundle);
        }
      }
    }

    remotePackage.setBundles(bundles);

    HashSet<PackageTag> tags = new HashSet<>();
    if (plugin.getTags() != null) {
      for (String tag : plugin.getTags()) {
        tags.add(new PackageTag(tag, remotePackage));
      }
    }
    remotePackage.setTags(tags);

    return remotePackage;
  }

  /**
   * Creates a {@link PackageBundle} entity from a {@link OASFile}.
   *
   * @param file bundle json bundleMapper
   * @return bundle entity
   */
  public static PackageBundle mapperToEntity(OASFile file) {

    PackageBundle packageBundle = new PackageBundle();
    packageBundle.setName(file.getFormat() + " - " + file.getType());
    packageBundle.setDownloadUrl(file.getUrl());
    packageBundle.setDownloadSha256(file.getSha256());

    List<String> targets = new ArrayList<>();
    for (OASFile.System system : file.getSystems()) {
      for (String arch : file.getArchitectures()) {
        targets.add(system.getType() + "-" + arch);
      }
    }
    packageBundle.setTargets(targets);
    packageBundle.setFileSize(file.getSize());

    // TODO: Bind the file format to the Plugin format
    if (file.getContains() != null && file.getContains().size() > 0) {
      List<String> formats = new ArrayList<>(file.getContains());
      packageBundle.setFormats(formats);
    }

    return packageBundle;
  }

}
