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

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SideBar extends VBox {

  private Animation hideSidebar;
  private Animation showSidebar;

  /**
   * Creates a sidebar containing a vertical alignment of the given nodes.
   */
  public SideBar(final double expandedWidth, Node node) {
    this.setPrefWidth(expandedWidth);

    // create a bar to hide and show.
    setAlignment(Pos.TOP_CENTER);
    VBox.setVgrow(node, Priority.ALWAYS);
    getChildren().add(node);

    this.managedProperty().bind(this.visibleProperty());

    // create an animation to hide the sidebar.
    hideSidebar = new Transition() {
      {
        setCycleDuration(Duration.millis(250));
      }

      protected void interpolate(double frac) {
        final double curWidth = expandedWidth * (1.0 - frac);
        setPrefWidth(curWidth);
        setTranslateX(-expandedWidth + curWidth);
        setOpacity(1.0 - frac);
      }
    };
    hideSidebar.onFinishedProperty().set(e -> setVisible(false));

    // create an animation to show the sidebar.
    showSidebar = new Transition() {
      {
        setCycleDuration(Duration.millis(250));
      }

      protected void interpolate(double frac) {
        final double curWidth = expandedWidth * frac;
        setPrefWidth(curWidth);
        setTranslateX(-expandedWidth + curWidth);
        setOpacity(frac);
      }
    };

  }

  public void collapse() {
    hideSidebar.play();
  }

  public void expand() {
    setVisible(true);
    showSidebar.play();
  }

  /**
   * Return true if the sidebar is collapsed, false otherwise.
   * 
   * @return sidebar collapse sate
   */
  public boolean isCollapsed() {
    return (showSidebar.statusProperty().get() == Animation.Status.STOPPED
        && hideSidebar.statusProperty().get() == Animation.Status.STOPPED && !isVisible());
  }

  /**
   * Toggle the sidebar state. For example, the sidebar is collapsed if it was
   * previsously expanded.
   */
  public void toggle() {
    if (isCollapsed()) {
      expand();
    } else {
      collapse();
    }
  }

}