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

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 * Rippler inspired by JFXRippler from JFoenix.
 */
@Deprecated
public class Rippler extends StackPane {
  public enum RipplerPos {
    FRONT, BACK
  }

  public enum RipplerMask {
    CIRCLE, RECT, FIT
  }

  protected RippleGenerator rippler;
  protected Pane ripplerPane;
  protected Node control;

  protected static final double RIPPLE_MAX_RADIUS = 300;

  private boolean enabled = true;
  private boolean forceOverlay = false;
  private Interpolator rippleInterpolator = Interpolator.SPLINE(0.0825,
          0.3025,
          0.0875,
          0.9975); //0.1, 0.54, 0.28, 0.95);

  public Rippler() {
    this(null, RipplerMask.RECT, RipplerPos.FRONT);
  }

  public Rippler(Node control) {
    this(control, RipplerMask.RECT, RipplerPos.FRONT);
  }

  public Rippler(Node control, RipplerPos pos) {
    this(control, RipplerMask.RECT, pos);
  }

  public Rippler(Node control, RipplerMask mask) {
    this(control, mask, RipplerPos.FRONT);
  }

  public Rippler(Node control, RipplerMask mask, RipplerPos pos) {
    initialize();

    setMaskType(mask);
    setPosition(pos);
    createRippleUI();
    setControl(control);

    position.addListener(observable -> updateControlPosition());

    setPickOnBounds(false);
    setCache(true);
    setCacheHint(CacheHint.SPEED);
    setCacheShape(true);

    this.ripplerFillProperty().set(Color.WHITESMOKE);
  }

  protected final void createRippleUI() {
    rippler = new RippleGenerator();
    ripplerPane = new StackPane();
    ripplerPane.setMouseTransparent(true);
    ripplerPane.getChildren().add(rippler);
    getChildren().add(ripplerPane);
  }

  public void setControl(Node control) {
    if (control != null) {
      this.control = control;
      positionControl(control);
      initControlListeners();
    }
  }

  protected void positionControl(Node control) {
    if (this.position.get() == RipplerPos.BACK) {
      getChildren().add(control);
    } else {
      getChildren().add(0, control);
    }
  }

  protected void updateControlPosition() {
    if (this.position.get() == RipplerPos.BACK) {
      ripplerPane.toBack();
    } else {
      ripplerPane.toFront();
    }
  }

  public Node getControl() {
    return control;
  }

  public void setEnabled(boolean enable) {
    this.enabled = enable;
  }

  public static void updateBackground(Background newBackground, Region nodeToUpdate, Paint fill) {
    if (newBackground != null && !newBackground.getFills().isEmpty()) {
      final BackgroundFill[] fills = new BackgroundFill[newBackground.getFills().size()];
      for (int i = 0; i < newBackground.getFills().size(); i++) {
        BackgroundFill bf = newBackground.getFills().get(i);
        fills[i] = new BackgroundFill(fill, bf.getRadii(), bf.getInsets());
      }
      nodeToUpdate.setBackground(new Background(fills));
    }
  }

  protected Node getMask() {
    double borderWidth = ripplerPane.getBorder() != null ? ripplerPane.getBorder().getInsets().getTop() : 0;
    Bounds bounds = control.getBoundsInParent();
    double width = control.getLayoutBounds().getWidth();
    double height = control.getLayoutBounds().getHeight();
    double diffMinX = Math.abs(control.getBoundsInLocal().getMinX() - control.getLayoutBounds().getMinX());
    double diffMinY = Math.abs(control.getBoundsInLocal().getMinY() - control.getLayoutBounds().getMinY());
    double diffMaxX = Math.abs(control.getBoundsInLocal().getMaxX() - control.getLayoutBounds().getMaxX());
    double diffMaxY = Math.abs(control.getBoundsInLocal().getMaxY() - control.getLayoutBounds().getMaxY());
    Node mask;
    switch (getMaskType()) {
      case RECT -> mask = new Rectangle(bounds.getMinX() + diffMinX - snappedLeftInset(),
              bounds.getMinY() + diffMinY - snappedTopInset(),
              width - 2 * borderWidth,
              height - 2 * borderWidth); // -0.1 to prevent resizing the anchor pane
      case CIRCLE -> {
        double radius = Math.min((width / 2) - 2 * borderWidth, (height / 2) - 2 * borderWidth);
        mask = new Circle((bounds.getMinX() + diffMinX + bounds.getMaxX() - diffMaxX) / 2 - snappedLeftInset(),
                (bounds.getMinY() + diffMinY + bounds.getMaxY() - diffMaxY) / 2 - snappedTopInset(),
                radius,
                Color.BLUE);
      }
      case FIT -> {
        mask = new Region();
        if (control instanceof Shape) {
          ((Region) mask).setShape((Shape) control);
        } else if (control instanceof Region) {
          ((Region) mask).setShape(((Region) control).getShape());
          updateBackground(((Region) control).getBackground(), (Region) mask, Color.BLACK);
        }
        mask.resize(width, height);
        mask.relocate(bounds.getMinX() + diffMinX, bounds.getMinY() + diffMinY);
      }
      default -> mask = new Rectangle(bounds.getMinX() + diffMinX - snappedLeftInset(),
              bounds.getMinY() + diffMinY - snappedTopInset(),
              width - 2 * borderWidth,
              height - 2 * borderWidth); // -0.1 to prevent resizing the anchor pane
    }
    return mask;
  }

  protected double computeRippleRadius() {
    double width2 = control.getLayoutBounds().getWidth() * control.getLayoutBounds().getWidth();
    double height2 = control.getLayoutBounds().getHeight() * control.getLayoutBounds().getHeight();
    return Math.min(Math.sqrt(width2 + height2), RIPPLE_MAX_RADIUS) * 1.1 + 5;
  }

  protected void setOverLayBounds(Rectangle overlay) {
    overlay.setWidth(control.getLayoutBounds().getWidth());
    overlay.setHeight(control.getLayoutBounds().getHeight());
  }

  protected void initControlListeners() {
    // if the control got resized the overlay rect must be rest
    control.layoutBoundsProperty().addListener(observable -> resetRippler());
    if (getChildren().contains(control)) {
      control.boundsInParentProperty().addListener(observable -> resetRippler());
    }
    control.addEventHandler(MouseEvent.MOUSE_PRESSED,
            (event) -> createRipple(event.getX(), event.getY()));
    // create fade out transition for the ripple
    control.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> releaseRipple());
  }

  protected void createRipple(double x, double y) {
    if (!isRipplerDisabled()) {
      rippler.setGeneratorCenterX(x);
      rippler.setGeneratorCenterY(y);
      rippler.createRipple();
    }
  }

  protected void releaseRipple() {
    rippler.releaseRipple();
  }

  /**
   * show/hide the ripple overlay
   *
   * @param visible
   * @param forceOverlay used to hold the overlay after ripple action
   */
  public void setOverlayVisible(boolean visible, boolean forceOverlay) {
    this.forceOverlay = forceOverlay;
    setOverlayVisible(visible);
  }

  /**
   * show/hide the ripple overlay.
   * NOTE: setting overlay visibility to false will reset forceOverlay to false.
   *
   * @param visible visibility boolean
   */
  public void setOverlayVisible(boolean visible) {
    if (visible) {
      showOverlay();
    } else {
      forceOverlay = !visible ? false : forceOverlay;
      hideOverlay();
    }
  }

  private void showOverlay() {
    if (rippler.overlayRect != null) {
      rippler.overlayRect.outAnimation.stop();
    }
    rippler.createOverlay();
    rippler.overlayRect.inAnimation.play();
  }

  private void hideOverlay() {
    if (!forceOverlay) {
      if (rippler.overlayRect != null) {
        rippler.overlayRect.inAnimation.stop();
      }
      if (rippler.overlayRect != null) {
        rippler.overlayRect.outAnimation.play();
      }
    } else {
      System.err.println("Ripple Overlay is forced!");
    }
  }

  /**
   * Generates ripples on the screen every 0.3 seconds or whenever
   * the createRipple method is called. Ripples grow and fade out
   * over 0.6 seconds
   */
  final class RippleGenerator extends Group {

    private double generatorCenterX = 0;
    private double generatorCenterY = 0;
    private OverLayRipple overlayRect;
    private AtomicBoolean generating = new AtomicBoolean(false);
    private boolean cacheRipplerClip = false;
    private boolean resetClip = false;
    private Queue<Ripple> ripplesQueue = new LinkedList<Ripple>();

    RippleGenerator() {
      // improve in performance, by preventing
      // redrawing the parent when the ripple effect is triggered
      this.setManaged(false);
      this.setCache(true);
      this.setCacheHint(CacheHint.SPEED);
    }

    void createRipple() {
      if (enabled) {
        if (!generating.getAndSet(true)) {
          // create overlay once then change its color later
          createOverlay();
          if (this.getClip() == null || (getChildren().size() == 1 && !cacheRipplerClip) || resetClip) {
            this.setClip(getMask());
          }
          this.resetClip = false;

          // create the ripple effect
          final Ripple ripple = new Ripple(generatorCenterX, generatorCenterY);
          getChildren().add(ripple);
          ripplesQueue.add(ripple);

          // animate the ripple
          overlayRect.outAnimation.stop();
          overlayRect.inAnimation.play();
          ripple.inAnimation.play();
        }
      }
    }

    private void releaseRipple() {
      Ripple ripple = ripplesQueue.poll();
      if (ripple != null) {
        ripple.inAnimation.stop();
        ripple.outAnimation = new Timeline(
                new KeyFrame(Duration.millis(Math.min(800, (0.9 * 500) / ripple.getScaleX())),
                        ripple.outKeyValues));
        ripple.outAnimation.setOnFinished((event) -> getChildren().remove(ripple));
        ripple.outAnimation.play();
        if (generating.getAndSet(false)) {
          if (overlayRect != null) {
            overlayRect.inAnimation.stop();
            if (!forceOverlay) {
              overlayRect.outAnimation.play();
            }
          }
        }
      }
    }

    void cacheRippleClip(boolean cached) {
      cacheRipplerClip = cached;
    }

    void createOverlay() {
      if (overlayRect == null) {
        overlayRect = new OverLayRipple();
        overlayRect.setClip(getMask());
        getChildren().add(0, overlayRect);
        overlayRect.fillProperty().bind(Bindings.createObjectBinding(() -> {
          if (ripplerFill.get() instanceof Color) {
            return new Color(((Color) ripplerFill.get()).getRed(),
                    ((Color) ripplerFill.get()).getGreen(),
                    ((Color) ripplerFill.get()).getBlue(),
                    0.2);
          } else {
            return Color.TRANSPARENT;
          }
        }, ripplerFill));
      }
    }

    void setGeneratorCenterX(double generatorCenterX) {
      this.generatorCenterX = generatorCenterX;
    }

    void setGeneratorCenterY(double generatorCenterY) {
      this.generatorCenterY = generatorCenterY;
    }

    private final class OverLayRipple extends Rectangle {
      // Overlay ripple animations
      Animation inAnimation = new Timeline(new KeyFrame(Duration.millis(300),
              new KeyValue(opacityProperty(), 1, Interpolator.EASE_IN)));

      Animation outAnimation = new Timeline(new KeyFrame(Duration.millis(300),
              new KeyValue(opacityProperty(), 0, Interpolator.EASE_OUT)));

      OverLayRipple() {
        super();
        setOverLayBounds(this);
        this.getStyleClass().add("rippler-overlay");
        // update initial position
        if (Rippler.this.getChildrenUnmodifiable().contains(control)) {
          double diffMinX = Math.abs(control.getBoundsInLocal().getMinX() - control.getLayoutBounds().getMinX());
          double diffMinY = Math.abs(control.getBoundsInLocal().getMinY() - control.getLayoutBounds().getMinY());
          Bounds bounds = control.getBoundsInParent();
          this.setX(bounds.getMinX() + diffMinX - snappedLeftInset());
          this.setY(bounds.getMinY() + diffMinY - snappedTopInset());
        }
        // set initial attributes
        setOpacity(0);
        setCache(true);
        setCacheHint(CacheHint.SPEED);
        setCacheShape(true);
        setManaged(false);
      }
    }

    private final class Ripple extends Circle {

      KeyValue[] outKeyValues;
      Animation outAnimation = null;
      Animation inAnimation = null;

      private Ripple(double centerX, double centerY) {
        super(centerX,
                centerY,
                ripplerRadius.get().doubleValue() == Region.USE_COMPUTED_SIZE
                        ? computeRippleRadius() : ripplerRadius.get().doubleValue(), null);
        setCache(true);
        setCacheHint(CacheHint.SPEED);
        setCacheShape(true);
        setManaged(false);
        setSmooth(true);

        KeyValue[] inKeyValues = new KeyValue[isRipplerRecenter() ? 4 : 2];
        outKeyValues = new KeyValue[isRipplerRecenter() ? 5 : 3];

        inKeyValues[0] = new KeyValue(scaleXProperty(), 0.9, rippleInterpolator);
        inKeyValues[1] = new KeyValue(scaleYProperty(), 0.9, rippleInterpolator);

        outKeyValues[0] = new KeyValue(this.scaleXProperty(), 1, rippleInterpolator);
        outKeyValues[1] = new KeyValue(this.scaleYProperty(), 1, rippleInterpolator);
        outKeyValues[2] = new KeyValue(this.opacityProperty(), 0, rippleInterpolator);

        if (isRipplerRecenter()) {
          double dx = (control.getLayoutBounds().getWidth() / 2 - centerX) / 1.55;
          double dy = (control.getLayoutBounds().getHeight() / 2 - centerY) / 1.55;
          inKeyValues[2] = outKeyValues[3] = new KeyValue(translateXProperty(),
                  Math.signum(dx) * Math.min(Math.abs(dx),
                          this.getRadius() / 2),
                  rippleInterpolator);
          inKeyValues[3] = outKeyValues[4] = new KeyValue(translateYProperty(),
                  Math.signum(dy) * Math.min(Math.abs(dy),
                          this.getRadius() / 2),
                  rippleInterpolator);
        }
        inAnimation = new Timeline(new KeyFrame(Duration.ZERO,
                new KeyValue(scaleXProperty(),
                        0,
                        rippleInterpolator),
                new KeyValue(scaleYProperty(),
                        0,
                        rippleInterpolator),
                new KeyValue(translateXProperty(),
                        0,
                        rippleInterpolator),
                new KeyValue(translateYProperty(),
                        0,
                        rippleInterpolator),
                new KeyValue(opacityProperty(),
                        1,
                        rippleInterpolator)
        ), new KeyFrame(Duration.millis(900), inKeyValues));

        setScaleX(0);
        setScaleY(0);
        if (ripplerFill.get() instanceof Color) {
          Color circleColor = new Color(((Color) ripplerFill.get()).getRed(),
                  ((Color) ripplerFill.get()).getGreen(),
                  ((Color) ripplerFill.get()).getBlue(),
                  0.3);
          setStroke(circleColor);
          setFill(circleColor);
        } else {
          setStroke(ripplerFill.get());
          setFill(ripplerFill.get());
        }
      }
    }

    public void clear() {
      getChildren().clear();
      rippler.overlayRect = null;
      generating.set(false);
    }
  }

  private void resetOverLay() {
    if (rippler.overlayRect != null) {
      rippler.overlayRect.inAnimation.stop();
      final RippleGenerator.OverLayRipple oldOverlay = rippler.overlayRect;
      rippler.overlayRect.outAnimation.setOnFinished((finish) -> rippler.getChildren().remove(oldOverlay));
      rippler.overlayRect.outAnimation.play();
      rippler.overlayRect = null;
    }
  }

  private void resetClip() {
    this.rippler.resetClip = true;
  }

  protected void resetRippler() {
    resetOverLay();
    resetClip();
  }

  private static final String DEFAULT_STYLE_CLASS = "rippler";

  private void initialize() {
    this.getStyleClass().add(DEFAULT_STYLE_CLASS);
  }

  private SimpleObjectProperty<Boolean> ripplerRecenter = new SimpleObjectProperty<>(
          Rippler.this,
          "ripplerRecenter",
          false);

  public Boolean isRipplerRecenter() {
    return ripplerRecenter != null && ripplerRecenter.get();
  }

  public SimpleObjectProperty<Boolean> ripplerRecenterProperty() {
    return this.ripplerRecenter;
  }

  public void setRipplerRecenter(Boolean radius) {
    this.ripplerRecenter.set(radius);
  }

  /**
   * the ripple radius size, by default it will be automatically computed.
   */
  private SimpleObjectProperty<Number> ripplerRadius = new SimpleObjectProperty<>(
          Rippler.this,
          "ripplerRadius",
          Region.USE_COMPUTED_SIZE);

  public Number getRipplerRadius() {
    return ripplerRadius == null ? Region.USE_COMPUTED_SIZE : ripplerRadius.get();
  }

  public SimpleObjectProperty<Number> ripplerRadiusProperty() {
    return this.ripplerRadius;
  }

  public void setRipplerRadius(Number radius) {
    this.ripplerRadius.set(radius);
  }

  /**
   * the default color of the ripple effect
   */
  private SimpleObjectProperty<Paint> ripplerFill = new SimpleObjectProperty<>(
          Rippler.this,
          "ripplerFill",
          Color.rgb(0,
                  200,
                  255));

  public Paint getRipplerFill() {
    return ripplerFill == null ? Color.rgb(0, 200, 255) : ripplerFill.get();
  }

  public SimpleObjectProperty<Paint> ripplerFillProperty() {
    return this.ripplerFill;
  }

  public void setRipplerFill(Paint color) {
    this.ripplerFill.set(color);
  }

  /**
   * mask property used for clipping the rippler.
   * can be either CIRCLE/RECT
   */
  private SimpleObjectProperty<RipplerMask> maskType = new SimpleObjectProperty<>(
          Rippler.this,
          "maskType",
          RipplerMask.RECT);

  public RipplerMask getMaskType() {
    return maskType == null ? RipplerMask.RECT : maskType.get();
  }

  public SimpleObjectProperty<RipplerMask> maskTypeProperty() {
    return this.maskType;
  }

  public void setMaskType(RipplerMask type) {
    this.maskType.set(type);
  }


  /**
   * the ripple disable, by default it's false.
   * if true the ripple effect will be hidden
   */
  private SimpleBooleanProperty ripplerDisabled = new SimpleBooleanProperty(
          Rippler.this,
          "ripplerDisabled",
          false);

  public Boolean isRipplerDisabled() {
    return ripplerDisabled != null && ripplerDisabled.get();
  }

  public SimpleBooleanProperty ripplerDisabledProperty() {
    return this.ripplerDisabled;
  }

  public void setRipplerDisabled(Boolean disabled) {
    this.ripplerDisabled.set(disabled);
  }


  protected ObjectProperty<RipplerPos> position = new SimpleObjectProperty<>();

  public void setPosition(RipplerPos pos) {
    this.position.set(pos);
  }

  public RipplerPos getPosition() {
    return position == null ? RipplerPos.FRONT : position.get();
  }

  public ObjectProperty<RipplerPos> positionProperty() {
    return this.position;
  }

}