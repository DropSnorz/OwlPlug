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

package com.owlplug.core.ui.skins;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.utils.JFXNodeUtils;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.TreeShowingExpression;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.skin.ProgressIndicatorSkin;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Custom progress bar Skin based on JFoenix skin implementation.
 * This class fixes the usage of NodeHelper.treeShowingProperty() method. This property
 * is not available since JavaFx 17.
 * TODO : For a long term solution, JFoenix should be updated or replaced in OwlPlug.
 */
public class OwlPlugProgressBarSkin extends ProgressIndicatorSkin {

  private StackPane track;
  private StackPane secondaryBar;
  private StackPane bar;
  private double barWidth = 0;
  private double secondaryBarWidth = 0;
  private Animation indeterminateTransition;
  private Region clip;
  boolean wasIndeterminate = false;

  private TreeShowingExpression treeShowingExpression;

  public OwlPlugProgressBarSkin(JFXProgressBar bar) {
    super(bar);
    bar.widthProperty().addListener(observable -> {
      updateProgress();
      updateSecondaryProgress();
    });

    this.treeShowingExpression = new TreeShowingExpression(bar);

    registerChangeListener(bar.progressProperty(), (obs) -> updateProgress());
    registerChangeListener(bar.secondaryProgressProperty(), obs-> updateSecondaryProgress());
    registerChangeListener(bar.visibleProperty(), obs->updateAnimation());
    registerChangeListener(bar.parentProperty(), obs->updateAnimation());
    registerChangeListener(bar.sceneProperty(), obs->updateAnimation());

    unregisterChangeListeners(treeShowingExpression);
    unregisterChangeListeners(bar.indeterminateProperty());

    registerChangeListener(treeShowingExpression, obs -> this.updateAnimation());
    registerChangeListener(bar.indeterminateProperty(), obs->initialize());

    initialize();

    getSkinnable().requestLayout();
  }

  protected void initialize() {

    track = new StackPane();
    track.getStyleClass().setAll("track");

    bar = new StackPane();
    bar.getStyleClass().setAll("bar");

    secondaryBar = new StackPane();
    secondaryBar.getStyleClass().setAll("secondary-bar");

    clip = new Region();
    clip.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
    bar.backgroundProperty().addListener(observable -> JFXNodeUtils.updateBackground(bar.getBackground(), clip));

    getChildren().setAll(track, secondaryBar, bar);
  }

  @Override
  public double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
    return Node.BASELINE_OFFSET_SAME_AS_HEIGHT;
  }

  @Override
  protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
    return Math.max(100, leftInset + bar.prefWidth(getSkinnable().getWidth()) + rightInset);
  }

  @Override
  protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return topInset + bar.prefHeight(width) + bottomInset;
  }

  @Override
  protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
    return getSkinnable().prefWidth(height);
  }

  @Override
  protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return getSkinnable().prefHeight(width);
  }

  @Override
  protected void layoutChildren(double x, double y, double w, double h) {
    track.resizeRelocate(x, y, w, h);
    secondaryBar.resizeRelocate(x, y, secondaryBarWidth, h);
    bar.resizeRelocate(x, y, getSkinnable().isIndeterminate() ? w : barWidth, h);
    clip.resizeRelocate(0,0, w, h);

    if (getSkinnable().isIndeterminate()) {
      createIndeterminateTimeline();
      if (NodeHelper.isTreeShowing(getSkinnable())) {
        indeterminateTransition.play();
      }
      // apply clip
      bar.setClip(clip);
    } else if (indeterminateTransition != null) {
      clearAnimation();
      // remove clip
      bar.setClip(null);
    }
  }

  protected void updateSecondaryProgress() {
    final JFXProgressBar control = (JFXProgressBar) getSkinnable();
    secondaryBarWidth = ((int) (control.getWidth() - snappedLeftInset() - snappedRightInset()) * 2
            * Math.min(1, Math.max(0, control.getSecondaryProgress()))) / 2.0F;
    control.requestLayout();
  }

  protected void pauseTimeline(boolean pause) {
    if (getSkinnable().isIndeterminate()) {
      if (indeterminateTransition == null) {
        createIndeterminateTimeline();
      }
      if (pause) {
        indeterminateTransition.pause();
      } else {
        indeterminateTransition.play();
      }
    }
  }

  private void updateAnimation() {
    ProgressIndicator control = getSkinnable();
    final boolean isTreeVisible = control.isVisible() &&
            control.getParent() != null &&
            control.getScene() != null;
    if (indeterminateTransition != null) {
      pauseTimeline(!isTreeVisible);
    } else if (isTreeVisible) {
      createIndeterminateTimeline();
    }
  }

  private void updateProgress() {
    final ProgressIndicator control = getSkinnable();
    final boolean isIndeterminate = control.isIndeterminate();
    if (!(isIndeterminate && wasIndeterminate)) {
      barWidth = ((int) (control.getWidth() - snappedLeftInset() - snappedRightInset()) * 2
              * Math.min(1, Math.max(0, control.getProgress()))) / 2.0F;
      control.requestLayout();
    }
    wasIndeterminate = isIndeterminate;
  }

  private void createIndeterminateTimeline() {
    if (indeterminateTransition != null) {
      clearAnimation();
    }
    double dur = 1;
    ProgressIndicator control = getSkinnable();
    final double w = control.getWidth() - (snappedLeftInset() + snappedRightInset());
    indeterminateTransition = new Timeline(new KeyFrame(
            Duration.ZERO,
            new KeyValue(clip.scaleXProperty(), 0.0, Interpolator.EASE_IN),
            new KeyValue(clip.translateXProperty(), -w/2, Interpolator.LINEAR)
    ),
            new KeyFrame(
                    Duration.seconds(0.5* dur),
                    new KeyValue(clip.scaleXProperty(), 0.4, Interpolator.LINEAR)
            ),
            new KeyFrame(
                    Duration.seconds(0.9 * dur),
                    new KeyValue(clip.translateXProperty(), w/2, Interpolator.LINEAR)
            ),
            new KeyFrame(
                    Duration.seconds(1 * dur),
                    new KeyValue(clip.scaleXProperty(), 0.0, Interpolator.EASE_OUT)
            ));
    indeterminateTransition.setCycleCount(Timeline.INDEFINITE);
  }

  private void clearAnimation() {
    indeterminateTransition.stop();
    ((Timeline) indeterminateTransition).getKeyFrames().clear();
    indeterminateTransition = null;
  }

  @Override
  public void dispose() {
    super.dispose();

    treeShowingExpression.dispose();

    if (indeterminateTransition != null) {
      clearAnimation();
    }
  }
}