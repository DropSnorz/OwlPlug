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
 
package com.owlplug.core.ui;

import com.jfoenix.controls.JFXTreeCell;
import com.owlplug.core.model.IDirectory;
import com.owlplug.core.model.Plugin;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class CustomTreeCell extends JFXTreeCell<Object> {


  @Override
  public void updateItem(Object item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null) {
      setText(null);
      setGraphic(null);
    } else {
      if (item instanceof Plugin) {
        Plugin plugin = (Plugin) item;
        setText(null);
        HBox hBox = new HBox(4);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(getTreeItem().getGraphic());
        hBox.getChildren().add(new Label(plugin.getName()));
        Circle circle = new Circle(0, 0, 2);
        hBox.getChildren().add(circle);
        if(plugin.isNativeCompatible()) {
          circle.getStyleClass().add("shape-success");
        } else {
          circle.getStyleClass().add("shape-disabled");
        }
        setGraphic(hBox);
      } else if (item instanceof IDirectory) {
        setText(null);
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
      } else {
        setText(item.toString());
        setGraphic(getTreeItem().getGraphic());
      }
    }
  }

}
