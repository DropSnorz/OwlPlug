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

package com.owlplug.controls.skins;

import com.owlplug.controls.Popup;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

public class PopupSkin implements Skin<Popup> {

  protected Popup control;
  protected StackPane container = new StackPane();
  protected Region popupContent;
  protected Node root;

  private Animation animation;
  protected Scale scale;

  public PopupSkin(Popup control) {
    this.control = control;
    // set scale y to 0.01 instead of 0 to allow layout of the content,
    // otherwise it will cause exception in traverse engine, when focusing the 1st node
    scale = new Scale(1, 0.01, 0, 0);
    popupContent = control.getPopupContent();
    container.getStyleClass().add("popup-container");
    container.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    container.getChildren().add(popupContent);
    container.getTransforms().add(scale);
    root = new Pane(container);
    animation = getAnimation();
  }

  public void reset(Popup.PopupVPosition verticalAlign, Popup.PopupHPosition horizontalAlign,
                    double offsetX, double offsetY) {
    // position the popup according to its animation
    scale.setPivotX(horizontalAlign == Popup.PopupHPosition.RIGHT ? container.getWidth() : 0);
    scale.setPivotY(verticalAlign == Popup.PopupVPosition.BOTTOM ? container.getHeight() : 0);
    root.setTranslateX(horizontalAlign == Popup.PopupHPosition.RIGHT ? -container.getWidth() + offsetX : offsetX);
    root.setTranslateY(verticalAlign == Popup.PopupVPosition.BOTTOM ? -container.getHeight() + offsetY : offsetY);
  }

  public final void animate() {
    if (animation.getStatus() == Status.STOPPED) {
      animation.play();
    }
  }

  @Override
  public Popup getSkinnable() {
    return control;
  }

  @Override
  public Node getNode() {
    return root;
  }

  @Override
  public void dispose() {
    animation.stop();
    animation = null;
    container = null;
    control = null;
    popupContent = null;
    root = null;
  }

  protected Animation getAnimation() {
    return new Timeline(
        new KeyFrame(
            Duration.ZERO,
            new KeyValue(popupContent.opacityProperty(), 0, Interpolator.EASE_BOTH),
            new KeyValue(scale.xProperty(), 0, Interpolator.EASE_BOTH),
            new KeyValue(scale.yProperty(), 0, Interpolator.EASE_BOTH)
        ),
        new KeyFrame(Duration.millis(300),
            new KeyValue(scale.xProperty(), 1, Interpolator.EASE_BOTH),
            new KeyValue(popupContent.opacityProperty(), 0, Interpolator.EASE_BOTH)
        ),
        new KeyFrame(Duration.millis(400),
            new KeyValue(popupContent.opacityProperty(), 1, Interpolator.EASE_BOTH),
            new KeyValue(scale.yProperty(), 1, Interpolator.EASE_BOTH)
        )
    );

  }

  public void init() {
    animation.stop();
    scale.setX(1);
    scale.setY(0.1);
  }
}
