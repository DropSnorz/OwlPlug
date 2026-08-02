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

import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Wraps a control and overlays a small badge in its top-right corner.
 * The badge's text and visibility are fully caller-driven: this control has no
 * opinion on what the text means or when it should be shown.
 */
@DefaultProperty("content")
public class NotificationBadge extends StackPane {

  private final Label badgeLabel = new Label();
  private final ObjectProperty<Node> content = new SimpleObjectProperty<>();
  private final StringProperty text = new SimpleStringProperty();
  private final BooleanProperty badgeVisible = new SimpleBooleanProperty(false);
  private final DoubleProperty offsetX = new SimpleDoubleProperty(6);
  private final DoubleProperty offsetY = new SimpleDoubleProperty(-4);

  public NotificationBadge() {
    getStyleClass().add("notification-badge");
    // Hug wrapped content tightly: without this, a parent HBox/VBox's default
    // fillHeight/fillWidth stretches this StackPane to match a tall/wide sibling (e.g. a
    // wrapping chip row next to it), spreading badgeLabel (TOP_RIGHT) and content (CENTER)
    // apart inside the now-oversized box instead of keeping them pinned together.
    setMaxWidth(USE_PREF_SIZE);
    setMaxHeight(USE_PREF_SIZE);
    badgeLabel.getStyleClass().add("notification-badge-label");
    badgeLabel.setMouseTransparent(true);
    badgeLabel.textProperty().bind(text);
    badgeLabel.visibleProperty().bind(badgeVisible);
    badgeLabel.managedProperty().bind(badgeVisible);
    badgeLabel.translateXProperty().bind(offsetX);
    badgeLabel.translateYProperty().bind(offsetY);

    StackPane.setAlignment(badgeLabel, Pos.TOP_RIGHT);

    content.addListener((obs, old, node) -> {
      if (old != null) {
        getChildren().remove(old);
      }
      if (node != null) {
        // always inserted behind badgeLabel, regardless of when this listener fires
        getChildren().add(0, node);
      }
    });
    getChildren().add(badgeLabel);
  }

  public NotificationBadge(Node control) {
    this();
    setContent(control);
  }

  public ObjectProperty<Node> contentProperty() {
    return content;
  }

  public Node getContent() {
    return content.get();
  }

  public void setContent(Node node) {
    content.set(node);
  }

  public StringProperty textProperty() {
    return text;
  }

  public String getText() {
    return text.get();
  }

  public void setText(String value) {
    text.set(value);
  }

  public BooleanProperty badgeVisibleProperty() {
    return badgeVisible;
  }

  public boolean isBadgeVisible() {
    return badgeVisible.get();
  }

  public void setBadgeVisible(boolean value) {
    badgeVisible.set(value);
  }

  public DoubleProperty offsetXProperty() {
    return offsetX;
  }

  public double getOffsetX() {
    return offsetX.get();
  }

  public void setOffsetX(double value) {
    offsetX.set(value);
  }

  public DoubleProperty offsetYProperty() {
    return offsetY;
  }

  public double getOffsetY() {
    return offsetY.get();
  }

  public void setOffsetY(double value) {
    offsetY.set(value);
  }
}
