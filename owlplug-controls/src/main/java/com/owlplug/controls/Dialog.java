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

import com.owlplug.controls.transitions.CachedTransition;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Dialog class to display content on top of the scene.
 * Simplified version based on JFXDialog from JFoenix library.
 */
public class Dialog extends StackPane {
  public enum DialogTransition {
    CENTER, TOP, RIGHT, BOTTOM, LEFT, NONE
  }
  private StackPane contentHolder;

  private double offsetX = 0;
  private double offsetY = 0;

  private StackPane dialogContainer;
  private Region content;
  private Transition animation;

  EventHandler<? super MouseEvent> closeHandler = e -> close();

  /**
   * creates empty Dialog control with CENTER animation type.
   */
  public Dialog() {
    this(null, null, DialogTransition.CENTER);
  }

  /**
   * creates Dialog control with a specified animation type, the animation type.
   * can be one of the following:
   * <ul>
   * <li>CENTER</li>
   * <li>TOP</li>
   * <li>RIGHT</li>
   * <li>BOTTOM</li>
   * <li>LEFT</li>
   * </ul>
   * @param dialogContainer is the parent of the dialog, it
   * @param content         the content of dialog
   * @param transitionType  the animation type
   */

  public Dialog(StackPane dialogContainer, Region content, DialogTransition transitionType) {
    initialize();
    setContent(content);
    setDialogContainer(dialogContainer);
    this.transitionType = transitionType;
    // init change listeners
    initChangeListeners();
  }

  private void initChangeListeners() {
    overlayCloseProperty().addListener((o, oldVal, newVal) -> {
      if (newVal) {
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler);
      } else {
        this.removeEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler);
      }
    });
  }

  private void initialize() {
    this.setVisible(false);
    this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    contentHolder = new StackPane();
    contentHolder.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
    contentHolder.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.26), 25, 0.25, 0, 8));
    contentHolder.setPickOnBounds(false);
    // ensure holder is never resized beyond it's preferred size
    contentHolder.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    this.getChildren().add(contentHolder);
    this.getStyleClass().add("dialog-overlay-pane");
    StackPane.setAlignment(contentHolder, Pos.CENTER);
    this.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.5), null, null)));
    // close the dialog if clicked on the overlay pane
    if (overlayClose.get()) {
      this.addEventHandler(MouseEvent.MOUSE_PRESSED, closeHandler);
    }
    // prevent propagating the events to overlay pane
    contentHolder.addEventHandler(MouseEvent.ANY, Event::consume);
  }


  public StackPane getDialogContainer() {
    return dialogContainer;
  }

  /**
   * Set the dialog container.
   * The dialog container must be StackPane, it's the container for the dialog to be shown in.
   *
   * @param dialogContainer dialogContainer
   */
  public void setDialogContainer(StackPane dialogContainer) {
    if (dialogContainer != null) {
      this.dialogContainer = dialogContainer;
      offsetX = dialogContainer.getBoundsInLocal().getWidth();
      offsetY = dialogContainer.getBoundsInLocal().getHeight();
      animation = getShowAnimation(transitionType);
    }
  }

  public Region getContent() {
    return content;
  }

  public void setContent(Region content) {
    if (content != null) {
      this.content = content;
      this.content.setPickOnBounds(false);
      contentHolder.getChildren().setAll(content);
    }
  }

  private final BooleanProperty overlayClose = new SimpleBooleanProperty(true);

  public final BooleanProperty overlayCloseProperty() {
    return this.overlayClose;
  }

  public final boolean isOverlayClose() {
    return this.overlayCloseProperty().get();
  }

  public final void setOverlayClose(final boolean overlayClose) {
    this.overlayCloseProperty().set(overlayClose);
  }

  /**
   * if sets to true, the content of dialog container will be cached and replaced with an image
   * when displaying the dialog (better performance).
   * this is recommended if the content behind the dialog will not change during the showing
   * period
   */
  private final BooleanProperty cacheContainer = new SimpleBooleanProperty(false);

  public boolean isCacheContainer() {
    return cacheContainer.get();
  }

  public BooleanProperty cacheContainerProperty() {
    return cacheContainer;
  }

  public void setCacheContainer(boolean cacheContainer) {
    this.cacheContainer.set(cacheContainer);
  }

  public void show(StackPane dialogContainer) {
    this.setDialogContainer(dialogContainer);
    showDialog();
  }

  private ArrayList<Node> tempContent;


  public void show() {
    this.setDialogContainer(dialogContainer);
    showDialog();
  }

  private void showDialog() {
    if (dialogContainer == null) {
      throw new RuntimeException("ERROR: Dialog container is not set!");
    }
    if (isCacheContainer()) {
      tempContent = new ArrayList<>(dialogContainer.getChildren());

      SnapshotParameters snapShotparams = new SnapshotParameters();
      snapShotparams.setFill(Color.TRANSPARENT);
      WritableImage temp = dialogContainer.snapshot(snapShotparams,
              new WritableImage((int) dialogContainer.getWidth(),
                      (int) dialogContainer.getHeight()));
      ImageView tempImage = new ImageView(temp);
      tempImage.setCache(true);
      tempImage.setCacheHint(CacheHint.SPEED);
      dialogContainer.getChildren().setAll(tempImage, this);
    } else {
      //prevent error if opening an already opened dialog
      dialogContainer.getChildren().remove(this);
      tempContent = null;
      dialogContainer.getChildren().add(this);
    }

    if (animation != null) {
      animation.play();
    } else {
      setVisible(true);
      setOpacity(1);
      Event.fireEvent(Dialog.this, new DialogEvent(DialogEvent.OPENED));
    }
  }


  public void close() {
    if (animation != null) {
      animation.setRate(-1);
      animation.play();
      animation.setOnFinished(e -> closeDialog());
    } else {
      setOpacity(0);
      setVisible(false);
      closeDialog();
    }

  }

  private void closeDialog() {
    resetProperties();
    Event.fireEvent(Dialog.this, new DialogEvent(DialogEvent.CLOSED));
    if (tempContent == null) {
      dialogContainer.getChildren().remove(this);
    } else {
      dialogContainer.getChildren().setAll(tempContent);
    }
  }

  private Transition getShowAnimation(DialogTransition transitionType) {
    Transition animation = null;
    if (contentHolder != null) {
      switch (transitionType) {
      case LEFT:
        contentHolder.setScaleX(1);
        contentHolder.setScaleY(1);
        contentHolder.setTranslateX(-offsetX);
        animation = new LeftTransition();
        break;
      case RIGHT:
        contentHolder.setScaleX(1);
        contentHolder.setScaleY(1);
        contentHolder.setTranslateX(offsetX);
        animation = new RightTransition();
        break;
      case TOP:
        contentHolder.setScaleX(1);
        contentHolder.setScaleY(1);
        contentHolder.setTranslateY(-offsetY);
        animation = new TopTransition();
        break;
      case BOTTOM:
        contentHolder.setScaleX(1);
        contentHolder.setScaleY(1);
        contentHolder.setTranslateY(offsetY);
        animation = new BottomTransition();
        break;
      case CENTER:
        contentHolder.setScaleX(0);
        contentHolder.setScaleY(0);
        animation = new CenterTransition();
        break;
      default:
        contentHolder.setScaleX(1);
        contentHolder.setScaleY(1);
        contentHolder.setTranslateX(0);
        contentHolder.setTranslateY(0);
        break;
      }
    }
    if (animation != null) {
      animation.setOnFinished(finish ->
              Event.fireEvent(Dialog.this, new DialogEvent(DialogEvent.OPENED)));
    }
    return animation;
  }

  private void resetProperties() {
    this.setVisible(false);
    contentHolder.setTranslateX(0);
    contentHolder.setTranslateY(0);
    contentHolder.setScaleX(1);
    contentHolder.setScaleY(1);
  }

  private class LeftTransition extends CachedTransition {
    LeftTransition() {
      super(contentHolder, new Timeline(
              new KeyFrame(Duration.ZERO,
                      new KeyValue(contentHolder.translateXProperty(), -offsetX, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.visibleProperty(), false, Interpolator.EASE_BOTH)
              ),
              new KeyFrame(Duration.millis(10),
                      new KeyValue(Dialog.this.visibleProperty(), true, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.opacityProperty(), 0, Interpolator.EASE_BOTH)
              ),
              new KeyFrame(Duration.millis(1000),
                      new KeyValue(contentHolder.translateXProperty(), 0, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.opacityProperty(), 1, Interpolator.EASE_BOTH)
              ))
      );
      // reduce the number to increase the shifting , increase number to reduce shifting
      setCycleDuration(Duration.seconds(0.4));
      setDelay(Duration.seconds(0));
    }
  }

  private class RightTransition extends CachedTransition {
    RightTransition() {
      super(contentHolder, new Timeline(
              new KeyFrame(Duration.ZERO,
                      new KeyValue(contentHolder.translateXProperty(), offsetX, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.visibleProperty(), false, Interpolator.EASE_BOTH)
              ),
              new KeyFrame(Duration.millis(10),
                      new KeyValue(Dialog.this.visibleProperty(), true, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.opacityProperty(), 0, Interpolator.EASE_BOTH)
              ),
              new KeyFrame(Duration.millis(1000),
                      new KeyValue(contentHolder.translateXProperty(), 0, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.opacityProperty(), 1, Interpolator.EASE_BOTH)))
      );
      // reduce the number to increase the shifting , increase number to reduce shifting
      setCycleDuration(Duration.seconds(0.4));
      setDelay(Duration.seconds(0));
    }
  }

  private class TopTransition extends CachedTransition {
    TopTransition() {
      super(contentHolder, new Timeline(
              new KeyFrame(Duration.ZERO,
                      new KeyValue(contentHolder.translateYProperty(), -offsetY, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.visibleProperty(), false, Interpolator.EASE_BOTH)
              ),
              new KeyFrame(Duration.millis(10),
                      new KeyValue(Dialog.this.visibleProperty(), true, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.opacityProperty(), 0, Interpolator.EASE_BOTH)
              ),
              new KeyFrame(Duration.millis(1000),
                      new KeyValue(contentHolder.translateYProperty(), 0, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.opacityProperty(), 1, Interpolator.EASE_BOTH)))
      );
      // reduce the number to increase the shifting , increase number to reduce shifting
      setCycleDuration(Duration.seconds(0.4));
      setDelay(Duration.seconds(0));
    }
  }

  private class BottomTransition extends CachedTransition {
    BottomTransition() {
      super(contentHolder, new Timeline(
              new KeyFrame(Duration.ZERO,
                      new KeyValue(contentHolder.translateYProperty(), offsetY, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.visibleProperty(), false, Interpolator.EASE_BOTH)
              ),
              new KeyFrame(Duration.millis(10),
                      new KeyValue(Dialog.this.visibleProperty(), true, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.opacityProperty(), 0, Interpolator.EASE_BOTH)
              ),
              new KeyFrame(Duration.millis(1000),
                      new KeyValue(contentHolder.translateYProperty(), 0, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.opacityProperty(), 1, Interpolator.EASE_BOTH)))
      );
      // reduce the number to increase the shifting , increase number to reduce shifting
      setCycleDuration(Duration.seconds(0.4));
      setDelay(Duration.seconds(0));
    }
  }

  private class CenterTransition extends CachedTransition {
    CenterTransition() {
      super(contentHolder, new Timeline(
              new KeyFrame(Duration.ZERO,
                      new KeyValue(contentHolder.scaleXProperty(), 0, Interpolator.EASE_BOTH),
                      new KeyValue(contentHolder.scaleYProperty(), 0, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.visibleProperty(), false, Interpolator.EASE_BOTH)
              ),
              new KeyFrame(Duration.millis(10),
                      new KeyValue(Dialog.this.visibleProperty(), true, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.opacityProperty(), 0, Interpolator.EASE_BOTH)
              ),
              new KeyFrame(Duration.millis(1000),
                      new KeyValue(contentHolder.scaleXProperty(), 1, Interpolator.EASE_BOTH),
                      new KeyValue(contentHolder.scaleYProperty(), 1, Interpolator.EASE_BOTH),
                      new KeyValue(Dialog.this.opacityProperty(), 1, Interpolator.EASE_BOTH)
              ))
      );
      // reduce the number to increase the shifting , increase number to reduce shifting
      setCycleDuration(Duration.seconds(0.4));
      setDelay(Duration.seconds(0));
    }
  }

  private static final String DEFAULT_STYLE_CLASS = "dialog";

  private DialogTransition transitionType;

  public DialogTransition getTransitionType() {
    return transitionType == null ? DialogTransition.CENTER : transitionType;
  }

  public void setTransitionType(DialogTransition transition) {
    this.transitionType = transition;
  }

  @Override
  public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
    return getClassCssMetaData();
  }

  private final ObjectProperty<EventHandler<? super DialogEvent>> onDialogClosedProperty = new ObjectPropertyBase<>() {
    @Override
    protected void invalidated() {
      setEventHandler(DialogEvent.CLOSED, get());
    }

    @Override
    public Object getBean() {
      return Dialog.this;
    }

    @Override
    public String getName() {
      return "onClosed";
    }
  };

  /**
   * Defines a function to be called when the dialog is closed.
   * Note: it will be triggered after the close animation is finished.
   */
  public ObjectProperty<EventHandler<? super DialogEvent>> onDialogClosedProperty() {
    return onDialogClosedProperty;
  }

  public void setOnDialogClosed(EventHandler<? super DialogEvent> handler) {
    onDialogClosedProperty().set(handler);
  }

  public EventHandler<? super DialogEvent> getOnDialogClosed() {
    return onDialogClosedProperty().get();
  }


  private final ObjectProperty<EventHandler<? super DialogEvent>> onDialogOpenedProperty = new ObjectPropertyBase<>() {
    @Override
    protected void invalidated() {
      setEventHandler(DialogEvent.OPENED, get());
    }

    @Override
    public Object getBean() {
      return Dialog.this;
    }

    @Override
    public String getName() {
      return "onOpened";
    }
  };

  /**
   * Defines a function to be called when the dialog is opened.
   * Note: it will be triggered after the show animation is finished.
   */
  public ObjectProperty<EventHandler<? super DialogEvent>> onDialogOpenedProperty() {
    return onDialogOpenedProperty;
  }

  public void setOnDialogOpened(EventHandler<? super DialogEvent> handler) {
    onDialogOpenedProperty().set(handler);
  }

  public EventHandler<? super DialogEvent> getOnDialogOpened() {
    return onDialogOpenedProperty().get();
  }

  public class DialogEvent extends Event {

    private static final long serialVersionUID = 1L;

    /**
     * Construct a new Dialog {@code Event} with the specified event type.
     *
     * @param eventType the event type
     */
    public DialogEvent(EventType<? extends Event> eventType) {
      super(eventType);
    }

    /**
     * This event occurs when a Dialog is closed, no longer visible to the user.
     * ( after the exit animation ends )
     */
    public static final EventType<DialogEvent> CLOSED =
            new EventType<>(Event.ANY, "DIALOG_CLOSED");

    /**
     * This event occurs when a Dialog is opened, visible to the user.
     * ( after the entrance animation ends )
     */
    public static final EventType<DialogEvent> OPENED =
            new EventType<>(Event.ANY, "DIALOG_OPENED");


  }
}