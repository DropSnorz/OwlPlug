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


package com.owlplug.controls.transitions;

import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Applies animation on a cached node to improve the performance.
 * Based on CachedTransition from JFoenix Library.
 */
public class CachedTransition extends Transition {
  protected final Node node;
  protected ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>();
  private CacheMemento[] mementos = new CacheMemento[0];

  public CachedTransition(final Node node, final Timeline timeline) {
    this.node = node;
    this.timeline.set(timeline);
    mementos = node == null ? mementos : new CacheMemento[]{new CacheMemento(node)};
    statusProperty().addListener(observable -> {
      if (getStatus() == Status.RUNNING) {
        starting();
      } else {
        stopping();
      }
    });
  }

  public CachedTransition(final Node node, final Timeline timeline, CacheMemento... cacheMementos) {
    this.node = node;
    this.timeline.set(timeline);
    mementos = new CacheMemento[(node == null ? 0 : 1) + cacheMementos.length];
    if (node != null) {
      mementos[0] = new CacheMemento(node);
    }
    System.arraycopy(cacheMementos, 0, mementos, node == null ? 0 : 1, cacheMementos.length);
    statusProperty().addListener(observable -> {
      if (getStatus() == Status.RUNNING) {
        starting();
      } else {
        stopping();
      }
    });
  }

  /**
   * Called when the animation is starting.
   */
  protected void starting() {
    if (mementos != null) {
      for (CacheMemento memento : mementos) {
        memento.cache();
      }
    }
  }

  /**
   * Called when the animation is stopping.
   */
  protected void stopping() {
    if (mementos != null) {
      for (CacheMemento memento : mementos) {
        memento.restore();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void interpolate(double d) {
    timeline.get().playFrom(Duration.seconds(d));
    timeline.get().stop();
  }
}