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
 
package com.owlplug.core.ui;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.model.IDirectory;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginComponent;
import com.owlplug.core.model.PluginDirectory;
import com.owlplug.core.model.PluginState;
import com.owlplug.core.model.Symlink;
import com.owlplug.core.services.PluginService;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class PluginTreeCell extends TreeCell<Object> {

  private PluginService pluginService;
  private ApplicationDefaults applicationDefaults;

  public PluginTreeCell(ApplicationDefaults applicationDefaults, PluginService pluginService) {
    this.applicationDefaults = applicationDefaults;
    this.pluginService = pluginService;
  }


  @Override
  public void updateItem(Object item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null) {
      setText(null);
      setGraphic(null);
    } else {
      if (item instanceof Plugin) {
        renderPlugin((Plugin) item);
      } else if (item instanceof PluginComponent) {
        renderComponent((PluginComponent) item);
      } else if (item instanceof IDirectory) {
        renderDirectory((IDirectory) item);
      } else {
        setText(item.toString());
        setGraphic(getTreeItem().getGraphic());
      }
    }
    
    // Force the rendering immediately to avoid blinking nodes on the TreeView
    // Blinking appears since JavaFX14 migration
    this.applyCss();
  }

  private void renderPlugin(Plugin plugin) {
    HBox hbox = new HBox(4);
    hbox.setAlignment(Pos.CENTER_LEFT);
    hbox.getChildren().add(new ImageView(applicationDefaults.getPluginFormatIcon(plugin.getFormat())));
    hbox.getChildren().add(new Label(plugin.getName()));
    Circle circle = new Circle(0, 0, 2);
    hbox.getChildren().add(circle);

    PluginState state = pluginService.getPluginState(plugin);
    if (state.equals(PluginState.UNSTABLE)) {
      circle.getStyleClass().add("shape-state-unstable");
    } else if (state.equals(PluginState.ACTIVE)) {
      circle.getStyleClass().add("shape-state-active");
    } else if (state.equals(PluginState.DISABLED)) {
      circle.getStyleClass().add("shape-state-disabled");
    } else {
      circle.getStyleClass().add("shape-state-installed");
    }

    circle.applyCss();

    if (plugin.isDisabled()) {
      Label label = new Label("(disabled)");
      label.getStyleClass().add("label-disabled");
      hbox.getChildren().add(label);
    }
    setGraphic(hbox);
    setText(null);
  }


  private void renderComponent(PluginComponent pluginComponent) {
    Label label = new Label(pluginComponent.getName());
    label.setGraphic(new ImageView(applicationDefaults.pluginComponentImage));
    setGraphic(label);
    setText(null);
  }


  private void renderDirectory(IDirectory dir) {
    TextFlow textFlow = new TextFlow();
    Text directoryName;

    if (dir.getDisplayName() != null && !dir.getName().equals(dir.getDisplayName())) {
      String preText = dir.getDisplayName().replaceAll("/" + dir.getName() + "$", "");
      Text pre = new Text(preText);
      pre.getStyleClass().add("text-disabled");
      textFlow.getChildren().add(pre);
      directoryName = new Text("/" + dir.getName());

    } else {
      directoryName = new Text(dir.getName());
    }

    if (dir.isStale()) {
      directoryName.getStyleClass().add("text-danger");
      directoryName.setText(dir.getName() + " (Stale)");
    }

    textFlow.getChildren().add(directoryName);

    Node icon;
    if (dir instanceof Symlink) {
      icon = new ImageView(applicationDefaults.symlinkImage);
    } else if (dir instanceof PluginDirectory pluginDirectory) {
      if (pluginDirectory.isRootDirectory()) {
        icon = new ImageView(applicationDefaults.rootDirectoryImage);
      } else {
        icon = new ImageView(applicationDefaults.directoryImage);
      }
    } else {
      icon = new ImageView(applicationDefaults.directoryImage);
    }
    HBox hbox = new HBox(5);
    hbox.getChildren().add(icon);
    hbox.getChildren().add(textFlow);

    setGraphic(hbox);
    setText(null);
  }

}
