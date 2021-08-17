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

import com.jfoenix.controls.JFXTreeCell;
import com.owlplug.core.model.IDirectory;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginState;
import com.owlplug.core.services.PluginService;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class PluginTreeCell extends JFXTreeCell<Object> {

  private PluginService pluginService;

  public PluginTreeCell(PluginService pluginService){
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
        Plugin plugin = (Plugin) item;
        HBox hbox = new HBox(4);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().add(getTreeItem().getGraphic());
        hbox.getChildren().add(new Label(plugin.getName()));
        Circle circle = new Circle(0, 0, 2);
        hbox.getChildren().add(circle);

        PluginState state = pluginService.getPluginState(plugin);
        if(state.equals(PluginState.ACTIVE)) {
          circle.getStyleClass().add("shape-state-active");
        } else if (state.equals(PluginState.DISABLED)) {
          circle.getStyleClass().add("shape-state-disabled");
        } else {
          circle.getStyleClass().add("shape-state-installed");
        }

        circle.applyCss();
        
        if (((Plugin) item).isDisabled()) {
          Label label = new Label("(disabled)");
          label.getStyleClass().add("label-disabled");
          hbox.getChildren().add(label);
        }
        setGraphic(hbox);
        setText(null);
      } else if (item instanceof IDirectory) {
        TextFlow textFlow = new TextFlow();
        IDirectory dir = (IDirectory) item;
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

        Node icon = getTreeItem().getGraphic();
        HBox hbox = new HBox(5);
        hbox.getChildren().add(icon);
        hbox.getChildren().add(textFlow);

        setGraphic(hbox);
        setText(null);
      } else {
        setText(item.toString());
        setGraphic(getTreeItem().getGraphic());
      }
    }
    
    // Force the rendering immediately to avoid blinking nodes on the TreeView
    // Blinking appears since JavaFX14 migration
    this.applyCss();
  }

}
