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

package com.owlplug.plugin.tasks.discovery;

import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.Symlink;
import com.owlplug.plugin.tasks.discovery.fileformats.PluginFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collect plugins and symlinks based on task scan properties.
 * Based on {@link ScopedScanEntityCollector} but adds differential collection capabilities.
 */
public class DifferentialScanEntityCollector extends ScopedScanEntityCollector {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private PluginFileDifferential pluginDifferential = new PluginFileDifferential();
  private SymlinkDifferential symlinkDifferential = new SymlinkDifferential();

  public DifferentialScanEntityCollector(PluginScanTaskParameters parameters) {
    super(parameters);
  }

  public DifferentialScanEntityCollector differentialPlugins(List<Plugin> original) {

    List<String> collected = this.getPluginFiles().stream()
            .map(PluginFile::getPath)
            .collect(Collectors.toList());

    List<String> persisted = original.stream()
            .map(Plugin::getPath)
            .collect(Collectors.toList());

    log.debug("Plugin differential, collected {} plugins", collected.size());
    log.debug("Plugin differential, persisted {} plugins", persisted.size());

    PathDifferential diff = differential(collected, persisted);

    pluginDifferential = new PluginFileDifferential();
    for (PluginFile file : getPluginFiles()) {
      if (diff.getAdded().contains(file.getPath())) {
        pluginDifferential.getAdded().add(file);
      }
    }
    pluginDifferential.setRemoved(diff.getRemoved());

    log.info("Plugin differential, added {} plugins", pluginDifferential.getAdded().size());
    log.info("Plugin differential, removed {} plugins", pluginDifferential.getRemoved().size());

    return this;

  }

  public DifferentialScanEntityCollector differentialSymlinks(List<Symlink> original) {

    List<String> collected = this.getSymlinks().stream()
            .map(Symlink::getPath)
            .collect(Collectors.toList());

    List<String> persisted = original.stream()
            .map(Symlink::getPath)
            .collect(Collectors.toList());

    log.debug("Symlink differential, collected {} symlinks", collected.size());
    log.debug("Symlink differential, persisted {} symlinks", persisted.size());

    PathDifferential diff = differential(collected, persisted);

    symlinkDifferential = new SymlinkDifferential();
    for (Symlink symlink : getSymlinks()) {
      if (diff.getAdded().contains(symlink.getPath())) {
        symlinkDifferential.getAdded().add(symlink);
      }
    }
    symlinkDifferential.setRemoved(diff.getRemoved());

    log.info("Symlink differential, added {} symlinks", symlinkDifferential.getAdded().size());
    log.info("Symlink differential, removed {} symlinks", symlinkDifferential.getRemoved().size());

    return this;
  }

  @Override
  public DifferentialScanEntityCollector collect() {
    super.collect();
    return this;
  }


  private PathDifferential differential(List<String> newList, List<String> oldList) {
    PathDifferential diff = new PathDifferential();
    List<String> added = new ArrayList<>(newList);
    added.removeAll(oldList);
    diff.setAdded(added);

    List<String> removed = new ArrayList<>(oldList);
    removed.removeAll(newList);
    diff.setRemoved(removed);

    return diff;
  }

  public PluginFileDifferential getPluginDifferential() {
    return pluginDifferential;
  }

  public SymlinkDifferential getSymlinkDifferential() {
    return symlinkDifferential;
  }

  public static final class PathDifferential {
    private List<String> added = new ArrayList<>();
    private List<String> removed = new ArrayList<>();

    public List<String> getAdded() {
      return added;
    }

    public void setAdded(List<String> added) {
      this.added = added;
    }

    public List<String> getRemoved() {
      return removed;
    }

    public void setRemoved(List<String> removed) {
      this.removed = removed;
    }
  }

  public static final class PluginFileDifferential {
    private List<PluginFile> added = new ArrayList<>();
    private List<String> removed = new ArrayList<>();

    public List<PluginFile> getAdded() {
      return added;
    }

    public void setAdded(List<PluginFile> added) {
      this.added = added;
    }

    public List<String> getRemoved() {
      return removed;
    }

    public void setRemoved(List<String> removed) {
      this.removed = removed;
    }
  }

  public static final class SymlinkDifferential {
    private List<Symlink> added = new ArrayList<>();
    private List<String> removed = new ArrayList<>();

    public List<Symlink> getAdded() {
      return added;
    }

    public void setAdded(List<Symlink> added) {
      this.added = added;
    }

    public List<String> getRemoved() {
      return removed;
    }

    public void setRemoved(List<String> removed) {
      this.removed = removed;
    }
  }


}
