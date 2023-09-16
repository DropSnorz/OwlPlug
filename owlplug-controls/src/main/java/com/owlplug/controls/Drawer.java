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


package com.owlplug.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class Drawer extends StackPane {

  private DoubleProperty defaultSizeProperty = new SimpleDoubleProperty();
  private StackPane content = new StackPane();
  private StackPane overlayPane = new StackPane();
  private StackPane sidePane = new StackPane();


  public Drawer() {
    overlayPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> close());
    sidePane.getStyleClass().add("drawer-side-pane");
    //overlayPane.setMouseTransparent(true);

  }

  public void open() {
    this.getChildren().add(overlayPane);
    sidePane.setPrefWidth(defaultSizeProperty.get());
    sidePane.setMaxWidth(defaultSizeProperty.get());
    this.getChildren().add(sidePane);
    StackPane.setAlignment(sidePane, Pos.TOP_LEFT);

  }

  public void close() {
    this.getChildren().remove(overlayPane);
    this.getChildren().remove(sidePane);

  }

  public void setSidePane(Node node) {
    this.sidePane.getChildren().setAll(node);
  }

  public void setContent(Node... content) {
    this.getChildren().setAll(content);
  }

  public ObservableList<Node> getContent() {
    return this.getChildren();
  }

  public double getDefaultDrawerSize() {
    return defaultSizeProperty.get();
  }

  public void setDefaultDrawerSize(double size) {
    defaultSizeProperty.set(size);
  }


}
