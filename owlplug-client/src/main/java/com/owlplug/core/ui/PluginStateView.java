package com.owlplug.core.ui;

import com.owlplug.core.model.PluginState;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Circle;

public class PluginStateView extends Label {

  public PluginStateView() {
    super("Unknown");
  }

  public void setPluginState(PluginState state) {

    Circle circle = new Circle(0, 0, 2);
    if (state.equals(PluginState.ACTIVE)) {
      circle.getStyleClass().add("shape-state-active");
    } else if (state.equals(PluginState.DISABLED)) {
      circle.getStyleClass().add("shape-state-disabled");
    } else {
      circle.getStyleClass().add("shape-state-installed");
    }
    this.setGraphic(circle);
    this.setContentDisplay(ContentDisplay.RIGHT);
    this.setText(state.getText());
    this.setTooltip(new Tooltip(state.getDescription()));

  }

}
