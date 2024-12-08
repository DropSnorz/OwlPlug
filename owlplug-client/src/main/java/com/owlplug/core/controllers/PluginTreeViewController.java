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

package com.owlplug.core.controllers;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ApplicationPreferences;
import com.owlplug.core.dao.SymlinkDAO;
import com.owlplug.core.model.IDirectory;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginComponent;
import com.owlplug.core.model.PluginDirectory;
import com.owlplug.core.model.Symlink;
import com.owlplug.core.services.PluginService;
import com.owlplug.core.ui.FilterableTreeItem;
import com.owlplug.core.ui.PluginTreeCell;
import com.owlplug.core.utils.FileUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PluginTreeViewController extends BaseController {

  @Autowired
  private PluginService pluginService;
  @Autowired
  private SymlinkDAO symlinkDAO;

  private final SimpleStringProperty search = new SimpleStringProperty();
  private final TreeView<Object> pluginTreeView;
  private final FilterableTreeItem<Object> treePluginNode;
  private final FilterableTreeItem<Object> treeFileRootNode;

  private PluginTreeViewController.FileTree pluginTree;


  public PluginTreeViewController() {
    pluginTreeView = new TreeView<>();
    treePluginNode = new FilterableTreeItem<>("(all)");
    treeFileRootNode = new FilterableTreeItem<>("(all)");

    pluginTreeView.setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
      @Override
      public TreeCell<Object> call(TreeView<Object> p) {
        return new PluginTreeCell(getApplicationDefaults(), pluginService);
      }
    });

    pluginTreeView.setRoot(treePluginNode);

    // Binds search property to plugin tree filter
    treePluginNode.predicateProperty().bind(Bindings.createObjectBinding(() -> {
      if (search.getValue() == null || search.getValue().isEmpty()) {
        return null;
      }
      return (item) -> {
        if (item instanceof Plugin) {
          Plugin plugin = (Plugin) item;
          return plugin.getName().toLowerCase().contains(search.getValue().toLowerCase())
                  || (plugin.getCategory() != null && plugin.getCategory().toLowerCase().contains(search.getValue().toLowerCase()));
        } else {
          return item.toString().toLowerCase().contains(search.getValue().toLowerCase());
        }
      };
    }, search));

    // Binds search property to file tree filter
    treeFileRootNode.predicateProperty().bind(Bindings.createObjectBinding(() -> {
      if (search.getValue() == null || search.getValue().isEmpty()) {
        return null;
      }
      return (item) -> {
        if (item instanceof Plugin) {
          Plugin plugin = (Plugin) item;
          return plugin.getName().toLowerCase().contains(search.getValue().toLowerCase())
                  || (plugin.getCategory() != null && plugin.getCategory().toLowerCase().contains(search.getValue().toLowerCase()));
        } else {
          return item.toString().toLowerCase().contains(search.getValue().toLowerCase());
        }
      };
    }, search));

  }

  public SimpleStringProperty searchProperty() {
    return this.search;
  }

  public TreeView<Object> getTreeView() {
    return pluginTreeView;
  }

  public void refresh() {
    pluginTreeView.refresh();
  }

  public void setDisplayMode(Display display) {
    if (Display.DirectoryTree.equals(display)) {
      pluginTreeView.setRoot(treeFileRootNode);
    } else if (Display.FlatTree.equals(display)) {
      pluginTreeView.setRoot(treePluginNode);
    }
  }

  public void setPlugins(Iterable<Plugin> plugins) {
    this.clearAndFillPluginTree(plugins);
  }

  /**
   * Refreshes displayed plugins in tree views.
   */
  private void clearAndFillPluginTree(Iterable<Plugin> plugins) {

    treePluginNode.getInternalChildren().clear();


    for (Plugin plugin : plugins) {

      FilterableTreeItem<Object> item = new FilterableTreeItem<Object>(plugin);
      treePluginNode.getInternalChildren().add(item);

      // Display subcomponents in the plugin tree
      if (plugin.getComponents().size() > 1) {
        for (PluginComponent component : plugin.getComponents()) {
          FilterableTreeItem<Object> compItem = new FilterableTreeItem<>(component);
          item.getInternalChildren().add(compItem);
        }
      }
    }

    treePluginNode.setExpanded(true);

    treeFileRootNode.getInternalChildren().clear();

    generatePluginTree(plugins);

    Set<String> userPluginDirectories = new HashSet<>();
    ApplicationPreferences prefs = this.getPreferences();
    if (prefs.getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false)
            && !prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, "").isBlank()) {
      String path = prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, "");
      userPluginDirectories.add(FileUtils.convertPath(path));
      userPluginDirectories.addAll(prefs.getList(ApplicationDefaults.VST2_EXTRA_DIRECTORY_KEY));
    }

    if (prefs.getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false)
            && !prefs.get(ApplicationDefaults.VST3_DIRECTORY_KEY, "").isBlank()) {
      String path = prefs.get(ApplicationDefaults.VST3_DIRECTORY_KEY, "");
      userPluginDirectories.add(FileUtils.convertPath(path));
      userPluginDirectories.addAll(prefs.getList(ApplicationDefaults.VST3_EXTRA_DIRECTORY_KEY));
    }

    if (prefs.getBoolean(ApplicationDefaults.AU_DISCOVERY_ENABLED_KEY, false)
            && !prefs.get(ApplicationDefaults.AU_DIRECTORY_KEY, "").isBlank()) {
      String path = prefs.get(ApplicationDefaults.AU_DIRECTORY_KEY, "");
      userPluginDirectories.add(FileUtils.convertPath(path));
      userPluginDirectories.addAll(prefs.getList(ApplicationDefaults.AU_EXTRA_DIRECTORY_KEY));
    }

    if (prefs.getBoolean(ApplicationDefaults.LV2_DISCOVERY_ENABLED_KEY, false)
            && !prefs.get(ApplicationDefaults.LV2_DIRECTORY_KEY, "").isBlank()) {
      String path = prefs.get(ApplicationDefaults.LV2_DIRECTORY_KEY, "");
      userPluginDirectories.add(FileUtils.convertPath(path));
      userPluginDirectories.addAll(prefs.getList(ApplicationDefaults.LV2_EXTRA_DIRECTORY_KEY));
    }

    for (String directory : userPluginDirectories) {
      treeFileRootNode.getInternalChildren().add(initDirectoryRoot(pluginTree, directory));
    }

    treeFileRootNode.setExpanded(true);

  }

  /**
   * Generates a PluginTree representation.
   * <pre>
   * [rootDir ->
   *   [ subDir1 -> [ plugin1 -> [] ],
   *     subDir2 -> [ plugin2 -> [] , plugin3 -> [] ]
   *   ]
   * ]
   * </pre>
   *
   */
  private void generatePluginTree(Iterable<Plugin> plugins) {

    pluginTree = new PluginTreeViewController.FileTree();

    for (Plugin plug : plugins) {
      PluginTreeViewController.FileTree node = pluginTree;
      String[] subDirs = plug.getPath().split("/");
      String currentPath = "";
      for (int i = 0; i < subDirs.length; i++) {
        currentPath = currentPath + subDirs[i] + "/";
        String segment = subDirs[i];
        PluginTreeViewController.FileTree ft = new PluginTreeViewController.FileTree();

        if (node.get(segment) == null) {
          // Node is a plugin (End of branch)
          if (i == subDirs.length - 1) {
            ft.setNodeValue(plug);

            // Node is a directory
          } else {
            // TODO Should be optimized for large plugin set
            List<Plugin> localPluginList = new ArrayList<Plugin>();
            for (Plugin p : plugins) {
              if (p.getPath().startsWith(currentPath)) {
                localPluginList.add(p);
              }
            }

            // Retrieve Symlink if exist
            // TODO: This can be refactored to prevent trailing slash removal
            Symlink symlink = symlinkDAO.findByPath(currentPath.substring(0, currentPath.length() - 1));
            if (symlink != null) {
              symlink.setPluginList(localPluginList);
              ft.setNodeValue(symlink);
            } else {
              PluginDirectory directory = new PluginDirectory();
              directory.setName(segment);
              directory.setPath(currentPath);
              directory.setPluginList(localPluginList);
              ft.setNodeValue(directory);
            }

          }
          node.put(segment, ft);
        }
        node = node.get(segment);
      }
    }
  }

  private FilterableTreeItem<Object> initDirectoryRoot(PluginTreeViewController.FileTree pluginTree, String directoryPath) {

    FilterableTreeItem<Object> item = new FilterableTreeItem<>(null);
    item.setExpanded(true);

    PluginTreeViewController.FileTree treeHead = pluginTree;
    String[] directories = directoryPath.split("/");

    for (String dir : directories) {
      if (treeHead != null) {
        treeHead = treeHead.get(dir);
      }
    }

    if (treeHead != null && treeHead.getNodeValue() instanceof PluginDirectory) {
      PluginDirectory directory = (PluginDirectory) treeHead.getNodeValue();
      directory.setRootDirectory(true);
      item.setValue(directory);
      buildDirectoryTree(treeHead, item, "");
    }

    return item;

  }

  /**
   * Builds the directory tree view using filetree representation. If some
   * directories contains only one subdirectory and nothing else, they are merged
   * together in one node.
   *
   * @param pluginTree   File tree representation
   * @param node         root tree node
   * @param mergedParent Name of merged parent tree
   */
  private void buildDirectoryTree(PluginTreeViewController.FileTree pluginTree, FilterableTreeItem<Object> node, String mergedParent) {

    String mergedParentName = mergedParent;
    node.setExpanded(true);

    if (mergedParentName == null) {
      mergedParentName = "";
    }

    // For each subdirectory (aka child nodes)
    for (String dir : pluginTree.keySet()) {
      PluginTreeViewController.FileTree child = pluginTree.get(dir);
      // If child is empty then we have reached a plugin and we can't go deeper
      if (child.values().isEmpty()) {
        Plugin plugin = (Plugin) child.getNodeValue();
        FilterableTreeItem<Object> plugItem = new FilterableTreeItem<>(plugin);
        node.getInternalChildren().add(plugItem);

        // Display subcomponents in the directory tree
        if (plugin.getComponents().size() > 1) {
          for (PluginComponent component : plugin.getComponents()) {
            FilterableTreeItem<Object> compItem = new FilterableTreeItem<>(component);
            plugItem.getInternalChildren().add(compItem);
          }
        }

        // If not we are exploring a directory
      } else {
        IDirectory directory;
        // If child node contains only one directory we can merge it with the child node
        if (child.size() == 1 && ((PluginTreeViewController.FileTree) child.values().toArray()[0]).getNodeValue() instanceof PluginDirectory
                && !(node.getValue() instanceof Symlink)
                && !(child.getNodeValue() instanceof Symlink)) {

          directory = (IDirectory) child.getNodeValue();
          mergedParentName = mergedParentName + directory.getName() + "/";

          buildDirectoryTree(child, node, mergedParentName);
          // We don't want to merge next directories in the current iteration
          mergedParentName = "";

          // In case our child cannot be merged (contains not only one subdirectory)
        } else {
          directory = (IDirectory) child.getNodeValue();
          directory.setDisplayName(mergedParentName + directory.getName());

          // We don't want to merge next directories in the current iteration
          mergedParentName = "";
          FilterableTreeItem<Object> item = new FilterableTreeItem<>(directory);
          node.getInternalChildren().add(item);
          buildDirectoryTree(child, item, mergedParentName);
        }
      }
    }
  }

  public void selectPluginInTreeById(long id) {
    List<TreeItem> items = getNestedChildren(pluginTreeView.getRoot());

    for (TreeItem item : items) {
      if (item.getValue() instanceof Plugin plugin
              && plugin.getId().equals(id)) {
        int row = pluginTreeView.getRow(item);
        pluginTreeView.getSelectionModel().select(row);
      }
    }

  }

  private List<TreeItem> getNestedChildren(TreeItem item) {
    List<TreeItem> items = new ArrayList<>();
    items.add(item);

    List<TreeItem> children = new ArrayList<>(item.getChildren());
    for (TreeItem child : children) {
      items.addAll(getNestedChildren(child));
    }
    return items;
  }

  class FileTree extends HashMap<String, PluginTreeViewController.FileTree> {

    private Object nodeValue;

    public void setNodeValue(Object nodeValue) {
      this.nodeValue = nodeValue;
    }

    public Object getNodeValue() {
      return nodeValue;
    }
  }

  enum Display {
    DirectoryTree, FlatTree
  }

}
