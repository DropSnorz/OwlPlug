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

import com.owlplug.controls.skins.PopupSkin;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Window;

/**
 * Popup class to display floating content.
 *
 * <p>
 * Simplified version based on JFXPopup from JFoenix library.
 * <a href="https://github.com/sshahine/JFoenix/blob/master/jfoenix/src/main/java/com/jfoenix/skins/JFXPopupSkin.java" />
 * </p>
 */
public class Popup extends PopupControl {

  public enum PopupHPosition {
    RIGHT, LEFT
  }

  public enum PopupVPosition {
    TOP, BOTTOM
  }

  public Popup() {
    this(null);
  }

  public Popup(Region content) {
    setPopupContent(content);
    initialize();
  }

  private void initialize() {
    this.setAutoFix(false);
    this.setAutoHide(true);
    this.setHideOnEscape(true);
    this.setConsumeAutoHidingEvents(false);
    this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    getScene().getRoot().setStyle("-fx-background-color: TRANSPARENT");
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new PopupSkin(this);
  }


  private final ObjectProperty<Region> popupContent = new SimpleObjectProperty<>(new Pane());

  public final ObjectProperty<Region> popupContentProperty() {
    return this.popupContent;
  }

  public final Region getPopupContent() {
    return this.popupContentProperty().get();
  }

  public final void setPopupContent(final Region popupContent) {
    this.popupContentProperty().set(popupContent);
  }

  /**
   * Show the popup using the default position.
   * @param node Inner node
   */
  public void show(Node node) {
    this.show(node, PopupVPosition.TOP, PopupHPosition.LEFT, 0, 0);
  }

  /**
   * Show the popup according to the specified position.
   *
   * @param verticalAlign can be TOP/BOTTOM
   * @param horizontalAlign can be LEFT/RIGHT
   */
  public void show(Node node, PopupVPosition verticalAlign, PopupHPosition horizontalAlign) {
    this.show(node, verticalAlign, horizontalAlign, 0, 0);
  }

  /**
   * Show the popup according to the specified position with a certain offset.
   *
   * @param verticalAlign      can be TOP/BOTTOM
   * @param horizontalAlign      can be LEFT/RIGHT
   * @param initOffsetX on the x axis
   * @param initOffsetY on the y axis
   */
  public void show(Node node, PopupVPosition verticalAlign,
                   PopupHPosition horizontalAlign, double initOffsetX, double initOffsetY) {
    if (!isShowing()) {
      if (node.getScene() == null || node.getScene().getWindow() == null) {
        throw new IllegalStateException("Can not show popup. The node must be attached to a scene/window.");
      }
      Window parent = node.getScene().getWindow();
      final Point2D origin = node.localToScene(0, 0);
      final double anchorX = parent.getX() + origin.getX()
                                 + node.getScene().getX()
                                 + (horizontalAlign == PopupHPosition.RIGHT ? ((Region) node).getWidth() : 0);
      final double anchorY = parent.getY() + origin.getY()
                                 + node.getScene().getY()
                                 + (verticalAlign == PopupVPosition.BOTTOM ? ((Region) node).getHeight() : 0);
      this.show(parent, anchorX, anchorY);
      ((PopupSkin) getSkin()).reset(verticalAlign, horizontalAlign, initOffsetX, initOffsetY);
      Platform.runLater(() -> ((PopupSkin) getSkin()).animate());
    }
  }

  public void show(Window window, double x, double y,
                   PopupVPosition verticalAlign, PopupHPosition horizontalAlign,
                   double initOffsetX, double initOffsetY) {
    if (!isShowing()) {
      if (window == null) {
        throw new IllegalStateException("Can not show popup. The node must be attached to a scene/window.");
      }
      final double anchorX = window.getX() + x + initOffsetX;
      final double anchorY = window.getY() + y + initOffsetY;
      this.show(window, anchorX, anchorY);
      ((PopupSkin) getSkin()).reset(verticalAlign, horizontalAlign, initOffsetX, initOffsetY);
      Platform.runLater(() -> ((PopupSkin) getSkin()).animate());
    }
  }

  @Override
  public void hide() {
    super.hide();
    ((PopupSkin) getSkin()).init();
  }

  private static final String DEFAULT_STYLE_CLASS = "popup";
}