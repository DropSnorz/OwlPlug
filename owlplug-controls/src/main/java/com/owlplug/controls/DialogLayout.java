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

import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DialogLayout extends VBox {
  private StackPane heading = new StackPane();
  private StackPane body = new StackPane();
  private FlowPane actions = new FlowPane();

  /**
   * Creates empty dialog layout.
   */
  public DialogLayout() {
    initialize();
    heading.getStyleClass().addAll("dialog-layout-heading", "title");
    body.getStyleClass().add("dialog-layout-body");
    VBox.setVgrow(body, Priority.ALWAYS);
    actions.getStyleClass().add("dialog-layout-actions");
    getChildren().setAll(heading, body, actions);
  }

  public ObservableList<Node> getHeading() {
    return heading.getChildren();
  }

  public void setHeading(Node... titleContent) {
    this.heading.getChildren().setAll(titleContent);
  }

  public ObservableList<Node> getBody() {
    return body.getChildren();
  }

  public void setBody(Node... body) {
    this.body.getChildren().setAll(body);
  }

  public ObservableList<Node> getActions() {
    return actions.getChildren();
  }

  public void setActions(Node... actions) {
    this.actions.getChildren().setAll(actions);
  }

  public void setActions(List<? extends Node> actions) {
    this.actions.getChildren().setAll(actions);
  }

  private static final String DEFAULT_STYLE_CLASS = "dialog-layout";

  private void initialize() {
    this.getStyleClass().add(DEFAULT_STYLE_CLASS);
  }
}