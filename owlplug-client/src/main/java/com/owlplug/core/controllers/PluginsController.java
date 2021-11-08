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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeView;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ApplicationPreferences;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.controllers.dialogs.NewLinkController;
import com.owlplug.core.dao.PluginDAO;
import com.owlplug.core.dao.SymlinkDAO;
import com.owlplug.core.model.IDirectory;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginDirectory;
import com.owlplug.core.model.Symlink;
import com.owlplug.core.services.PluginService;
import com.owlplug.core.ui.PluginTreeCell;
import com.owlplug.core.ui.FilterableTreeItem;
import com.owlplug.core.utils.FileUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PluginsController extends BaseController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private PluginService pluginService;
  @Autowired
  private PluginDAO pluginDAO;
  @Autowired
  private SymlinkDAO symlinkDAO;
  @Autowired
  private NodeInfoController nodeInfoController;
  @Autowired
  private NewLinkController newLinkController;
  @Autowired
  protected CoreTaskFactory taskFactory;

  @FXML
  private JFXButton syncButton;
  @FXML
  private JFXTreeView<Object> treeView;
  @FXML
  private JFXTabPane treeViewTabPane;
  @FXML
  private JFXTextField searchTextField;
  @FXML
  private JFXButton newLinkButton;

  private Iterable<Plugin> pluginList;
  private FileTree pluginTree;
  private FilterableTreeItem<Object> treePluginNode;
  private FilterableTreeItem<Object> treeFileRootNode;

  /**
   * FXML initialize method.
   */
  @FXML
  public void initialize() {

    newLinkButton.setOnAction(e -> {
      newLinkController.show();
    });

    treePluginNode = new FilterableTreeItem<>("(all)");
    treeFileRootNode = new FilterableTreeItem<>("(all)");

    treeView.setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
      @Override
      public TreeCell<Object> call(TreeView<Object> p) {
        return new PluginTreeCell(getApplicationDefaults(), pluginService);
      }
    });
    treeView.setRoot(treePluginNode);
    
    clearAndFillPluginTree();

    // Dispatches treeView selection event to the nodeInfoController
    treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        TreeItem<Object> selectedItem = newValue;
        nodeInfoController.setNode(selectedItem.getValue());
      }
    });

    // Handles tabPane selection event and toggles displayed treeView
    treeViewTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
      if (newTab.getId().equals("treeTabAll")) {
        treeView.setRoot(treePluginNode);
      } else {
        treeView.setRoot(treeFileRootNode);
      }
    });

    // Binds search property to plugin tree filter
    treePluginNode.predicateProperty().bind(Bindings.createObjectBinding(() -> {
      if (searchTextField.getText() == null || searchTextField.getText().isEmpty()) {
        return null;
      }
      return actor -> actor.toString().toLowerCase().contains(searchTextField.getText().toLowerCase());
    }, searchTextField.textProperty()));

    // Binds search property to plugin tree filter
    treeFileRootNode.predicateProperty().bind(Bindings.createObjectBinding(() -> {
      if (searchTextField.getText() == null || searchTextField.getText().isEmpty()) {
        return null;
      }
      return actor -> actor.toString().toLowerCase().contains(searchTextField.getText().toLowerCase());
    }, searchTextField.textProperty()));

    syncButton.setOnAction(e -> {
      this.getAnalyticsService().pageView("/app/core/action/syncPlugins");
      pluginService.syncPlugins();
    });

    taskFactory.addSyncPluginsListener(() -> clearAndFillPluginTree());

  }
  
  public void refreshPluginTree() {
    treeView.refresh();
  }

  /**
   * Refreshes displayed plugins in tree views.
   */
  public void clearAndFillPluginTree() {

    treePluginNode.getInternalChildren().clear();
    this.pluginList = pluginDAO.findAll();

    for (Plugin plugin : pluginList) {

      TreeItem<Object> item = new FilterableTreeItem<Object>(plugin);
      treePluginNode.getInternalChildren().add(item);
    }

    treePluginNode.setExpanded(true);

    treeFileRootNode.getInternalChildren().clear();

    generatePluginTree();

    Set<String> userPluginDirectories = new HashSet<>();
    ApplicationPreferences prefs = this.getPreferences();
    if (prefs.getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false)
        && !prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, "").isBlank()) {
      String path = prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, "");
      userPluginDirectories.add(FileUtils.convertPath(path));
      for(String extraDirectory : prefs.getList(ApplicationDefaults.VST2_EXTRA_DIRECTORY_KEY)) {
        userPluginDirectories.add(extraDirectory);
      }
    }

    if (prefs.getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false)
        && !prefs.get(ApplicationDefaults.VST3_DIRECTORY_KEY, "").isBlank()) {
      String path = prefs.get(ApplicationDefaults.VST3_DIRECTORY_KEY, "");
      userPluginDirectories.add(FileUtils.convertPath(path));
      for(String extraDirectory : prefs.getList(ApplicationDefaults.VST3_EXTRA_DIRECTORY_KEY)) {
        userPluginDirectories.add(extraDirectory);
      }
    }
    
    if (prefs.getBoolean(ApplicationDefaults.AU_DISCOVERY_ENABLED_KEY, false)
        && !prefs.get(ApplicationDefaults.AU_DIRECTORY_KEY, "").isBlank()) {
      String path = prefs.get(ApplicationDefaults.AU_DIRECTORY_KEY, "");
      userPluginDirectories.add(FileUtils.convertPath(path));
      for(String extraDirectory : prefs.getList(ApplicationDefaults.AU_EXTRA_DIRECTORY_KEY)) {
        userPluginDirectories.add(extraDirectory);
      }
    }

    for (String directory : userPluginDirectories) {
      treeFileRootNode.getInternalChildren().add(initDirectoryRoot(pluginTree, directory));
    }

    treeFileRootNode.setExpanded(true);

  }

  /**
   * Generates a PluginTree representation. [rootDir -> [ subDir1 -> [ plugin1 ->
   * [ ] ], subDir2 -> [ plugin2 -> [] , plugin3 -> [] ]] ]
   * 
   */
  private void generatePluginTree() {

    pluginTree = new FileTree();

    for (Plugin plug : pluginList) {
      FileTree node = pluginTree;
      String[] subDirs = plug.getPath().split("/");
      String currentPath = "";
      for (int i = 0; i < subDirs.length; i++) {
        currentPath = currentPath + subDirs[i] + "/";
        String segment = subDirs[i];
        FileTree ft = new FileTree();

        if (node.get(segment) == null) {
          // Node is a plugin (End of branch)
          if (i == subDirs.length - 1) {
            ft.setNodeValue(plug);

            // Node is a directory
          } else {
            // TODO Should be optimized for large plugin set
            List<Plugin> localPluginList = new ArrayList<Plugin>();
            for (Plugin p : pluginList) {
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

  private FilterableTreeItem<Object> initDirectoryRoot(FileTree pluginTree, String directoryPath) {

    FilterableTreeItem<Object> item = new FilterableTreeItem<>(null);
    item.setExpanded(true);

    FileTree treeHead = pluginTree;
    String[] directories = directoryPath.split("/");

    for (String dir : directories) {
      if (treeHead != null) {
        treeHead = treeHead.get(dir);
      }
    }

    if (treeHead != null) {
      item.setValue(treeHead.getNodeValue());
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
  private void buildDirectoryTree(FileTree pluginTree, FilterableTreeItem<Object> node, String mergedParent) {

    String mergedParentName = mergedParent;
    node.setExpanded(true);

    if (mergedParentName == null) {
      mergedParentName = "";
    }

    // For each subdirectory (aka child nodes)
    for (String dir : pluginTree.keySet()) {
      FileTree child = pluginTree.get(dir);
      // If child is empty then we have reached a plugin and we can't go deeper
      if (child.values().isEmpty()) {
        Plugin plugin = (Plugin) child.getNodeValue();
        FilterableTreeItem<Object> plugItem = new FilterableTreeItem<>(plugin);
        node.getInternalChildren().add(plugItem);
        // If not we are exploring a directory
      } else {
        IDirectory directory;
        // If child node contains only one directory we can merge it with the child node
        if (child.size() == 1 && ((FileTree) child.values().toArray()[0]).getNodeValue() instanceof PluginDirectory
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

  class FileTree extends HashMap<String, FileTree> {

    private Object nodeValue;

    public void setNodeValue(Object nodeValue) {
      this.nodeValue = nodeValue;
    }

    public Object getNodeValue() {
      return nodeValue;
    }
  }

}
