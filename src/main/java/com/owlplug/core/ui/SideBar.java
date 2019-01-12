package com.owlplug.core.ui;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    hideSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        setVisible(false);
      }
    });

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