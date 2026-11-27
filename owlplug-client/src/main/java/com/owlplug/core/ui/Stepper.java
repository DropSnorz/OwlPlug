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

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Horizontal step indicator: numbered circles linked by connectors, with a label under each one.
 * Steps before {@link #currentStepProperty()} are done, the current one is active (or failed when
 * {@link #errorProperty()} is set) and the next ones are pending. The meaning of each step is
 * left to the caller.
 */
public class Stepper extends HBox {

  /** Width of a step column, wide enough for the longest step label. */
  private static final double STEP_WIDTH = 64;
  /** Diameter of a step circle, must match .stepper-step-circle in owlplug.css. */
  private static final double STEP_CIRCLE_SIZE = 28;

  private static final PseudoClass ACTIVE = PseudoClass.getPseudoClass("active");
  private static final PseudoClass DONE = PseudoClass.getPseudoClass("done");
  private static final PseudoClass ERROR = PseudoClass.getPseudoClass("error");

  /** Nodes of a step. */
  private record StepView(VBox node, Label number, FontIcon check, FontIcon cross) {
  }

  private final ObservableList<String> steps = FXCollections.observableArrayList();
  private final IntegerProperty currentStep = new SimpleIntegerProperty(0);
  private final BooleanProperty error = new SimpleBooleanProperty(false);

  private final List<StepView> stepViews = new ArrayList<>();
  private final List<Region> connectors = new ArrayList<>();

  public Stepper() {
    getStyleClass().add("stepper");
    setAlignment(Pos.TOP_CENTER);
    steps.addListener((ListChangeListener<String>) change -> {
      build();
      update();
    });
    currentStep.addListener((obs, oldValue, newValue) -> update());
    error.addListener((obs, oldValue, newValue) -> update());
  }

  private void build() {
    getChildren().clear();
    stepViews.clear();
    connectors.clear();
    // Connectors extend under the neighbor steps up to their circles, whatever the label
    // widths, and are drawn behind the circles (opaque) so the line always meets them.
    double connectorOverlap = (STEP_WIDTH - STEP_CIRCLE_SIZE) / 2;
    int index = 1;
    for (String step : steps) {
      if (index > 1) {
        Region connector = new Region();
        connector.getStyleClass().add("stepper-connector");
        connector.setViewOrder(1);
        HBox.setMargin(connector, new Insets(0, -connectorOverlap, 0, -connectorOverlap));
        HBox.setHgrow(connector, Priority.ALWAYS);
        connectors.add(connector);
        getChildren().add(connector);
      }
      Label number = new Label(String.valueOf(index));
      number.getStyleClass().add("stepper-step-number");
      FontIcon check = new FontIcon("mdi2c-check");
      check.setIconSize(14);
      check.getStyleClass().add("stepper-step-check");
      FontIcon cross = new FontIcon("mdi2c-close");
      cross.setIconSize(14);
      cross.getStyleClass().add("stepper-step-cross");
      StackPane circle = new StackPane(number, check, cross);
      circle.getStyleClass().add("stepper-step-circle");

      Label label = new Label(step);
      label.getStyleClass().add("stepper-step-label");

      VBox node = new VBox(6, circle, label);
      node.setAlignment(Pos.TOP_CENTER);
      // Same width for every step, so circles are evenly spaced whatever their label
      node.setMinWidth(STEP_WIDTH);
      node.setPrefWidth(STEP_WIDTH);
      node.setMaxWidth(STEP_WIDTH);
      node.getStyleClass().add("stepper-step");
      stepViews.add(new StepView(node, number, check, cross));
      getChildren().add(node);
      index++;
    }
  }

  private void update() {
    int current = getCurrentStep();
    boolean failed = isError();
    for (int i = 0; i < stepViews.size(); i++) {
      StepView view = stepViews.get(i);
      view.node().pseudoClassStateChanged(DONE, i < current);
      view.node().pseudoClassStateChanged(ACTIVE, i == current && !failed);
      view.node().pseudoClassStateChanged(ERROR, i == current && failed);
      view.check().setVisible(i < current);
      view.cross().setVisible(i == current && failed);
      view.number().setVisible(!view.check().isVisible() && !view.cross().isVisible());
    }
    for (int i = 0; i < connectors.size(); i++) {
      // Connector i links step i and step i + 1
      connectors.get(i).pseudoClassStateChanged(DONE, i < current);
    }
  }

  /** Step labels, in order. */
  public ObservableList<String> getSteps() {
    return steps;
  }

  /**
   * Index of the current step. Previous steps are done, next ones are pending. A value
   * greater than or equal to the number of steps marks every step as done.
   */
  public IntegerProperty currentStepProperty() {
    return currentStep;
  }

  public int getCurrentStep() {
    return currentStep.get();
  }

  public void setCurrentStep(int value) {
    currentStep.set(value);
  }

  /** Whether the current step is displayed as failed instead of active. */
  public BooleanProperty errorProperty() {
    return error;
  }

  public boolean isError() {
    return error.get();
  }

  public void setError(boolean value) {
    error.set(value);
  }
}
