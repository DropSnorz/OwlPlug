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

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DoughnutChart extends PieChart {
  private final Circle innerCircle;

  public DoughnutChart() {
    super();

    innerCircle = new Circle();
    innerCircle.getStyleClass().add("doughnut-inner-circle");
  }

  @Override
  protected void layoutChartChildren(double top, double left, double contentWidth, double contentHeight) {
    super.layoutChartChildren(top, left, contentWidth, contentHeight);

    addInnerCircleIfNotPresent();
    updateInnerCircleLayout();
  }

  private void addInnerCircleIfNotPresent() {
    if (getData().size() > 0) {
      Node pie = getData().get(0).getNode();
      if (pie.getParent() instanceof Pane parent
              && !parent.getChildren().contains(innerCircle)) {
        parent.getChildren().add(innerCircle);
      }
    }
  }

  private void updateInnerCircleLayout() {
    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    double maxY = Double.MIN_VALUE;
    for (PieChart.Data data: getData()) {
      Node node = data.getNode();

      Bounds bounds = node.getBoundsInParent();
      if (bounds.getMinX() < minX) {
        minX = bounds.getMinX();
      }
      if (bounds.getMinY() < minY) {
        minY = bounds.getMinY();
      }
      if (bounds.getMaxX() > maxX) {
        maxX = bounds.getMaxX();
      }
      if (bounds.getMaxY() > maxY) {
        maxY = bounds.getMaxY();
      }
    }

    innerCircle.setCenterX(minX + (maxX - minX) / 2);
    innerCircle.setCenterY(minY + (maxY - minY) / 2);
    innerCircle.setRadius((maxX - minX) / 4);
    innerCircle.toFront();
  }
}